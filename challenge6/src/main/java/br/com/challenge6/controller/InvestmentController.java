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
        var newInvestment = investmentService.addInvestment(dto);
        var uri = builder.path("/{id}").buildAndExpand(newInvestment.id()).toUri();

        return ResponseEntity.created(uri).body(newInvestment);
    }

    @GetMapping
    public ResponseEntity<List<GetInvestmentDTO>> getUserInvestment(@RequestParam Long id){
        var investment = investmentService.getInvestmentsByUser(id);

        return ResponseEntity.ok().body(investment);
    }

    @GetMapping("/portfolio")
    public ResponseEntity<Double> getPortfolioValue(@RequestParam Long id){
        var value = investmentService.calculatePortfolioValue(id);

        return ResponseEntity.ok(value);
    }
}
