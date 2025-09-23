package br.com.challenge6.repository;

import br.com.challenge6.domain.investment.Investment;
import br.com.challenge6.domain.user.UserInvestmentsDTO;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface InvestmentRepository extends MongoRepository<Investment, Long> {
    List<Investment> findAllByUserId(Long id);


    @Aggregation(pipeline = {
            "{ $group: { _id: \"$userId\", investments: { $push: \"$$ROOT\" } } }",
            "{ $project: { userId: \"$_id\", investments: 1, _id: 0 } }"
    })
    List<UserInvestmentsDTO> findAllGroupedByUser();
}
