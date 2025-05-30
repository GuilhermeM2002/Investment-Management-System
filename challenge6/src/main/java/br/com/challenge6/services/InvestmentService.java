package br.com.challenge6.services;

import br.com.challenge6.domain.investment.AddInvestmentDTO;
import br.com.challenge6.domain.investment.GetInvestmentDTO;
import br.com.challenge6.domain.investment.Investment;
import br.com.challenge6.repository.InvestmentRepository;
import br.com.challenge6.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InvestmentService {
    @Autowired
    private InvestmentRepository investmentRepository;
    @Autowired
    private ModelMapper mapper;

    public AddInvestmentDTO addInvestment(AddInvestmentDTO dto){
        var investment = mapper.map(dto, Investment.class);
        var savedInvestment = investmentRepository.save(investment);

        return mapper.map(savedInvestment, AddInvestmentDTO.class);
    }

    public List<GetInvestmentDTO> getInvestmentsByUser(Long id) {
        var investments = investmentRepository.findAllByUserId(id);

        return investments.stream()
                .map(investment -> mapper.map(investment, GetInvestmentDTO.class))
                .toList();
    }

    public Double calculatePortfolioValue(Long id) {
        var investments = getInvestmentsByUser(id);
        double portfolioValue = 0.0;

        for (GetInvestmentDTO investment : investments) {
            if (investment != null && investment.currentPrice() != null && investment.quantity() != null) {
                portfolioValue += investment.currentPrice() * investment.quantity();
            }
        }

        return portfolioValue;
    }
}
