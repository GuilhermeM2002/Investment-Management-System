package br.com.challenge6.repository;

import br.com.challenge6.domain.investment.Investment;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface InvestmentRepository extends MongoRepository<Investment, Long> {
}
