package br.com.challenge6.controller;

import br.com.challenge6.domain.statementImport.ImportDTO;
import br.com.challenge6.services.StatementImportService;
import com.opencsv.exceptions.CsvException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/statement-import")
@RequiredArgsConstructor
public class StatementImportController {
    private final StatementImportService statementImportService;

    @PostMapping("/csv")
    public ResponseEntity importCsv(@RequestBody ImportDTO importDTO) throws IOException, CsvException {
        statementImportService.importCSV(importDTO.userId(), importDTO.filePath());

        return ResponseEntity.ok().build();
    }

    @PostMapping("/xlsx")
    public ResponseEntity importXlsx(@RequestBody ImportDTO importDTO) throws IOException {
        statementImportService.importXLSX(importDTO.userId(), importDTO.filePath());

        return ResponseEntity.ok().build();
    }
}
