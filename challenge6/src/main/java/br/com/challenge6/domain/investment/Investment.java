package br.com.challenge6.domain.investment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "investment")
public class Investment {
    @Id
    private Long id;
    private Long userId;
    private String assetType;
    private String ticker;
    private Double quantity;
    private Double buyPrice;
    private Double currentPrice;
    private LocalDate date;

    public Double getProfit() {
        return (currentPrice - buyPrice) * quantity;
    }
}
