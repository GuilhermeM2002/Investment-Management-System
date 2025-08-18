package br.com.challenge6.services;

import br.com.challenge6.domain.investment.StockDataNotFoundException;
import br.com.challenge6.domain.investment.StockPriceDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;

@Service
public class AlphaVantageService {
    @Value("${alpha.vantage.api.key}")
    private String apiKey;

    @Value("${alpha.vantage.base.url}")
    private String baseUrl;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper();

    public StockPriceDTO getDailyTimeSeries(String ticker){
        try {
            String url = String.format("%s?function=TIME_SERIES_DAILY&ticker=%s&apikey=%s",
                    baseUrl, ticker, apiKey);

            String response = restTemplate.getForObject(url, String.class);

            if (response == null){
                throw new StockDataNotFoundException(ticker);
            }

            JsonNode root = mapper.readTree(response);

            // Last date available
            String lastDate = root.path("Meta Data").path("3. Last Refreshed").asText();
            JsonNode lastData = root.path("Time Series (Daily)").path(lastDate);

            return new StockPriceDTO(
                    ticker,
                    LocalDate.parse(lastDate),
                    lastData.path("1. open").asDouble(),
                    lastData.path("2. high").asDouble(),
                    lastData.path("3. low").asDouble(),
                    lastData.path("4. close").asDouble(),
                    lastData.path("5. volume").asLong()
            );

        } catch (Exception e) {
            throw new StockDataNotFoundException(ticker, e);
        }
    }
}
