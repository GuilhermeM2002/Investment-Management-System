package br.com.challenge6.services;

import br.com.challenge6.domain.investment.AddInvestmentDTO;
import br.com.challenge6.domain.investment.GetInvestmentDTO;
import br.com.challenge6.domain.investment.Investment;
import br.com.challenge6.domain.investment.StockPriceDTO;
import br.com.challenge6.domain.user.UserInvestmentsDTO;
import br.com.challenge6.repository.InvestmentRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@Service
public class InvestmentService {
    @Autowired
    private InvestmentRepository investmentRepository;
    @Autowired
    private ModelMapper mapper;
    @Autowired
    private AlphaVantageService alphaVantageService;

    public AddInvestmentDTO addInvestment(AddInvestmentDTO dto){
        var investment = mapper.map(dto, Investment.class);
        investment.setId(UUID.randomUUID());
        var savedInvestment = investmentRepository.save(investment);

        return mapper.map(savedInvestment, AddInvestmentDTO.class);
    }

    public List<UserInvestmentsDTO> getAllInvestmentsGroupedByUser() {
        return investmentRepository.findAllGroupedByUser();
    }

    public List<GetInvestmentDTO> getInvestmentsByUser(Long id) {
        var investments = investmentRepository.findAllByUserId(id);

        return investments.stream()
                .map(investment -> mapper.map(investment, GetInvestmentDTO.class))
                .toList();
    }

    public double calculatePortfolioValue(Long id) {
        var investments = getInvestmentsByUser(id);
        double portfolioValue = 0.0;

        for (GetInvestmentDTO investment : investments) {
            if (investment != null && investment.getCurrentPrice() != null && investment.getQuantity() != null) {
                portfolioValue += investment.getCurrentPrice() * investment.getQuantity();
            }
        }

        return portfolioValue;
    }

    public StockPriceDTO getStockPrice(GetInvestmentDTO dto) {
        Investment investment = mapper.map(dto, Investment.class);
        StockPriceDTO stock = alphaVantageService.getDailyTimeSeries(investment.getTicker());

        double currentPrice = stock.getClose();
        double buyPrice = investment.getBuyPrice();
        double variation = ((currentPrice - buyPrice) / buyPrice) * 100;
        stock.setVariation(variation);
        return stock;
    }
}
