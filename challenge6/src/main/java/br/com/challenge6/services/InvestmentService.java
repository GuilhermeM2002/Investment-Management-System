package br.com.challenge6.services;

import br.com.challenge6.domain.investment.AddInvestmentDTO;
import br.com.challenge6.domain.investment.Investment;
import br.com.challenge6.repository.InvestmentRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InvestmentService {
    @Autowired
    private InvestmentRepository investmentRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private ModelMapper mapper;

    public AddInvestmentDTO addInvestment(AddInvestmentDTO dto){
        var investment = mapper.map(dto, Investment.class);
        var savedInvestment = investmentRepository.save(investment);

        return mapper.map(savedInvestment, AddInvestmentDTO.class);
    }

    public List<AddInvestmentDTO> getInvestmentsByUser(String userEmail){
        return null;
    }

    public Double calculatePortfolioValue(String userEmail){
        return null;
    }
}
