package br.com.challenge6.repository;

import br.com.challenge6.domain.statementImport.StatementImport;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface StatementImportRepository extends MongoRepository<StatementImport, Long> {
}
