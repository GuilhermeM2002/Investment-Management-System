package br.com.challenge6.domain.investment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class GetInvestmentDTO{
    private UUID id;
    private Long userId;
    private String assetType;
    private String ticker;
    private Double quantity;
    private Double buyPrice;
    private Double currentPrice;
    private LocalDate date;
}
