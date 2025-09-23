package br.com.challenge6.services;

import br.com.challenge6.domain.investment.GetInvestmentDTO;
import br.com.challenge6.domain.investment.Investment;
import br.com.challenge6.domain.investment.StockPriceDTO;
import br.com.challenge6.domain.user.UserInvestmentsDTO;
import br.com.challenge6.domain.user.UserResponse;
import br.com.challenge6.http.UserClient;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {
    @Autowired
    private InvestmentService investmentService;

    @Autowired
    private UserClient userClient;

    @Autowired
    private EmailService emailService;

    @Autowired
    private ModelMapper mapper;

    @Scheduled(cron = "0 0 9 * * ?")
    public void notifyLost() {
        List<UserInvestmentsDTO> groupedInvestments = investmentService.getAllInvestmentsGroupedByUser();

        for (UserInvestmentsDTO dto : groupedInvestments) {
            UserResponse user = userClient.getUserById(dto.getUserId());
            StringBuilder alertContent = new StringBuilder();
            boolean hasAlert = false;

            for (Investment inv : dto.getInvestments()) {
                StockPriceDTO stock = investmentService.getStockPrice(mapper.map(inv, GetInvestmentDTO.class));
                double variation = stock.getVariation();

                if (variation <= -10) {
                    hasAlert = true;
                    alertContent.append(String.format(
                            "📉 Alerta: %s caiu %.2f%% desde a compra.%n",
                            stock.getSymbol(),
                            variation
                    ));
                }
            }

            if (hasAlert) {
                emailService.sendEmail(user.email(), "📉 Alerta de Queda em Investimentos", alertContent.toString());
            }
        }
    }

    @Scheduled(cron = "0 0 7 * * ?")
    public void notifyHighGain() {
        List<UserInvestmentsDTO> groupedInvestments = investmentService.getAllInvestmentsGroupedByUser();

        for (UserInvestmentsDTO dto : groupedInvestments) {
            UserResponse user = userClient.getUserById(dto.getUserId());
            StringBuilder alertContent = new StringBuilder();
            boolean hasAlert = false;

            for (Investment inv : dto.getInvestments()) {
                StockPriceDTO stock = investmentService.getStockPrice(mapper.map(inv, GetInvestmentDTO.class));
                double variation = stock.getVariation();

                if (variation >= 15) {
                    hasAlert = true;
                    alertContent.append(String.format(
                            "📉 Alerta: %s subiu %.2f%% desde a compra.%n",
                            stock.getSymbol(),
                            variation
                    ));
                }
            }

            if (hasAlert) {
                emailService.sendEmail(user.email(), "📉 Alerta de Crescimento em Investimentos", alertContent.toString());
            }
        }
    }
}
