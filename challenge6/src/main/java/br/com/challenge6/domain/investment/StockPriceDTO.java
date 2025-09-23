package br.com.challenge6.domain.investment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class StockPriceDTO {
    private String symbol;
    private LocalDate date;
    private double variation;
    private double open;
    private double high;
    private double low;
    private double close;
    private long volume;
}
