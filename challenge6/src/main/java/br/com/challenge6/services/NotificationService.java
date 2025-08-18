package br.com.challenge6.services;

import br.com.challenge6.domain.investment.Investment;
import br.com.challenge6.domain.investment.StockPriceDTO;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Properties;

@Service
public class NotificationService {
    @Value("${email.username}")
    private String username;

    @Value("${email.password}")
    private String password;

    @Value("${email.smtp.host}")
    private String smtpHost;

    @Value("${email.smtp.port}")
    private String smtpPort;

    @Value("${email.smtp.auth}")
    private boolean smtpAuth;

    @Value("${email.smtp.starttls.enable}")
    private boolean startTls;

    @Autowired
    private AlphaVantageService alphaVantageService;

    public void sendEmail(String to, String subject, String content) {
        Properties props = new Properties();
        props.put("mail.smtp.auth", smtpAuth);
        props.put("mail.smtp.starttls.enable", startTls);
        props.put("mail.smtp.host", smtpHost);
        props.put("mail.smtp.port", smtpPort);

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(
                    Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setText(content);

            Transport.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Erro ao enviar e-mail: " + e.getMessage(), e);
        }
    }

    @Scheduled(cron = "0 0 9 * * ?")
    public void notifyLost(List<Investment> investments, String userEmail) {
        StringBuilder alertContent = new StringBuilder();
        boolean hasAlert = false;

        for (Investment inv : investments) {
            StockPriceDTO stock = alphaVantageService.getDailyTimeSeries(inv.getTicker());
            if (stock == null) continue; // segurança caso a API não retorne dados

            double currentPrice = stock.close();
            double buyPrice = inv.getBuyPrice();

            double variation = ((currentPrice - buyPrice) / buyPrice) * 100;

            if (variation <= -10) {
                hasAlert = true;
                alertContent.append(String.format(
                        "📉 Alerta: %s caiu %.2f%% desde a compra.%n" +
                                "Data: %s | Preço atual: R$%.2f | Comprado por: R$%.2f | Máxima: R$%.2f | Mínima: R$%.2f | Volume: %d%n%n",
                        stock.symbol(),
                        variation,
                        stock.date(),
                        stock.close(),
                        buyPrice,
                        stock.high(),
                        stock.low(),
                        stock.volume()
                ));
            }
        }

        if (hasAlert) {
            sendEmail(userEmail, "📉 Alerta de Queda em Investimentos", alertContent.toString());
        }
    }

    @Scheduled(cron = "0 0 9 * * ?")
    public void notifyHighGain(List<Investment> investmentList, String userEmail) {
        StringBuilder alertContent = new StringBuilder();
        boolean hasAlert = false;

        for (Investment inv : investmentList) {
            StockPriceDTO stock = alphaVantageService.getDailyTimeSeries(inv.getTicker());
            if (stock == null) continue;

            double currentPrice = stock.close();
            double buyPrice = inv.getBuyPrice();

            double variation = ((currentPrice - buyPrice) / buyPrice) * 100;

            if (variation >= 10) {
                hasAlert = true;
                alertContent.append(String.format(
                        "🚀 Alerta: %s cresceu %.2f%% desde a compra.%nData: %s | Preço atual: R$%.2f | Comprado por: R$%.2f | Máxima: R$%.2f | Mínima: R$%.2f%n",
                        stock.symbol(), variation, stock.date(), stock.close(), buyPrice, stock.high(), stock.low()
                ));
            }
        }

        if (hasAlert) {
            sendEmail(userEmail, "🚨 Alerta de Investimentos", alertContent.toString());
        }
    }
}
