package br.com.challenge6.controller;

import br.com.challenge6.domain.investment.AddInvestmentDTO;
import br.com.challenge6.domain.investment.GetInvestmentDTO;
import br.com.challenge6.services.InvestmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/investment")
@RequiredArgsConstructor
public class InvestmentController {
    private final InvestmentService investmentService;

    @PostMapping
    public ResponseEntity<AddInvestmentDTO> createInvestment(@RequestBody AddInvestmentDTO dto, UriComponentsBuilder builder){
        var uri = builder.path("/{id}").buildAndExpand(dto.id()).toUri();
        investmentService.addInvestment(dto);

        return ResponseEntity.created(uri).body(dto);
    }

    @GetMapping
    public ResponseEntity<List<GetInvestmentDTO>> getUserInvestment(@RequestParam String userEmail){
        var investment = investmentService.getInvestmentsByUser(userEmail);

        return ResponseEntity.ok().body(investment);
    }

    @GetMapping("/portfolio")
    public ResponseEntity<Double> getPortfolioValue(@RequestParam String userEmail){
        var value = investmentService.calculatePortfolioValue(userEmail);

        return ResponseEntity.ok(value);
    }
}
