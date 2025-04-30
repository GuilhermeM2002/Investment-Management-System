package br.com.challenge6.services;

import br.com.challenge6.domain.investment.AddInvestmentDTO;
import br.com.challenge6.domain.investment.Investment;
import br.com.challenge6.domain.statementImport.StatementImport;
import br.com.challenge6.repository.StatementImportRepository;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StatementImportService {
    @Autowired
    private StatementImportRepository statementImportRepository;

    @Autowired
    private InvestmentService investmentService;

    @Autowired
    private ModelMapper mapper;

    public void importCSV(Long userId, String filePath) throws IOException, CsvException {
        StatementImport stmt = createStatementImport(userId, filePath);

        try (Reader reader = Files.newBufferedReader(Paths.get(filePath));
             CSVReader csvReader = new CSVReader(reader)) {

            List<String[]> rows = csvReader.readAll();
            List<Investment> investments = rows.stream()
                    .skip(1)
                    .map(this::mapCsvRow)
                    .collect(Collectors.toList());

            validateData(investments);
            investments.forEach(inv -> {
                inv.setUserId(userId);
                investmentService.addInvestment(mapper.map(inv, AddInvestmentDTO.class));
            });

            stmt.setStatus("COMPLETED");
        } catch (Exception e) {
            stmt.setStatus("FAILED");
            throw e;
        } finally {
            statementImportRepository.save(stmt);
        }
    }

    public void importXLSX(Long userId, String filePath) throws IOException {
        StatementImport stmt = createStatementImport(userId, filePath);
        try (XSSFWorkbook workbook = new XSSFWorkbook(Files.newInputStream(Paths.get(filePath)))) {
            Sheet sheet = workbook.getSheetAt(0);
            List<Investment> investments = new ArrayList<>();

            for (Row row : sheet) {
                if (row.getRowNum() == 0) continue;
                investments.add(mapExcelRow(row));
            }

            validateData(investments);
            investments.forEach(inv -> {
                inv.setUserId(userId);
                investmentService.addInvestment(mapper.map(inv, AddInvestmentDTO.class));
            });

            stmt.setStatus("COMPLETED");
        } catch (Exception e) {
            stmt.setStatus("FAILED");
            throw e;
        } finally {
            statementImportRepository.save(stmt);
        }
    }

    public void validateData(List<Investment> investments) {
        for (Investment inv : investments) {
            if (inv.getQuantity() == null || inv.getQuantity() <= 0) {
                throw new IllegalArgumentException(
                        "Quantidade inválida para ticker " + inv.getTicker());
            }
            if (inv.getBuyPrice() == null || inv.getBuyPrice() <= 0) {
                throw new IllegalArgumentException(
                        "Preço de compra inválido para ticker " + inv.getTicker());
            }
            if (inv.getDate() == null || inv.getDate().isAfter(LocalDate.now())) {
                throw new IllegalArgumentException(
                        "Data inválida para ticker " + inv.getTicker());
            }
        }
    }

    private StatementImport createStatementImport(Long userId, String filePath) {
        StatementImport stmt = new StatementImport();
        stmt.setUserId(userId);
        stmt.setFileName(Paths.get(filePath).getFileName().toString());
        stmt.setImportDate(new Date());
        stmt.setStatus("IN_PROGRESS");
        return statementImportRepository.save(stmt);
    }

    private Investment mapCsvRow(String[] row) {
        Investment inv = new Investment();
        inv.setAssetType(row[0]);
        inv.setTicker(row[1]);
        inv.setQuantity(Double.parseDouble(row[2]));
        inv.setBuyPrice(Double.parseDouble(row[3]));
        inv.setDate(LocalDate.parse(row[4]));
        return inv;
    }

    private Investment mapExcelRow(Row row) {
        Investment inv = new Investment();
        inv.setAssetType(row.getCell(0).getStringCellValue());
        inv.setTicker(row.getCell(1).getStringCellValue());
        inv.setQuantity(row.getCell(2).getNumericCellValue());
        inv.setBuyPrice(row.getCell(3).getNumericCellValue());
        inv.setDate(row.getCell(4).getLocalDateTimeCellValue().toLocalDate());
        return inv;
    }
}
