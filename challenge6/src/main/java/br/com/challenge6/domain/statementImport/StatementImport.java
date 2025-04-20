package br.com.challenge6.domain.statementImport;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "statementImport")
public class StatementImport {
    @Id
    private Long id;
    private Long userId;
    private String fileName;
    private Date importDate;
    private String status;

    public void parseAndSave(){}
}
