package br.com.challenge6.domain.investment;

public class StockDataNotFoundException extends RuntimeException{
    public StockDataNotFoundException(String ticker) {
        super("Data not found for the ticker: " + ticker);
    }

    public StockDataNotFoundException(String ticker, Throwable cause) {
        super("Error when searching data for ticker: " + ticker, cause);
    }
}
