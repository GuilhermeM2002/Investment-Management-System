package br.com.challenge6.domain.investment;

import java.time.LocalDate;

public record StockPriceDTO(
        String symbol,
        LocalDate date,
        double open,
        double high,
        double low,
        double close,
        long volume) {
}
