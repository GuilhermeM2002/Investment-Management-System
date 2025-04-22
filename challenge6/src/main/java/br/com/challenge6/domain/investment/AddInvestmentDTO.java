package br.com.challenge6.domain.investment;

import java.time.LocalDate;

public record AddInvestmentDTO(
        Long id,
        Long userId,
        String assetType,
        String ticker,
        Double quantity,
        Double buyPrice,
        Double currentPrice,
        LocalDate date
) {
}
