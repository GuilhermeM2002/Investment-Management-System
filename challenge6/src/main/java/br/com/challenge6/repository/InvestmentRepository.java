package br.com.challenge6.repository;

import br.com.challenge6.domain.investment.Investment;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface InvestmentRepository extends MongoRepository<Investment, Long> {
    List<Investment> findAllByUserId(Long id);
}
