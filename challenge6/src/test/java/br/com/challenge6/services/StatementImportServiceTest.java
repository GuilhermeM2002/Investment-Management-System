package br.com.challenge6.services;

import br.com.challenge6.domain.investment.AddInvestmentDTO;
import br.com.challenge6.domain.investment.Investment;
import br.com.challenge6.domain.statementImport.StatementImport;
import br.com.challenge6.repository.StatementImportRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StatementImportServiceTest {
    @InjectMocks
    private StatementImportService statementImportService;

    @Mock
    private StatementImportRepository statementImportRepository;

    @Mock
    private InvestmentService investmentService;

    @Mock
    private ModelMapper modelMapper;

    @Captor
    private ArgumentCaptor<StatementImport> stmtCaptor;

    private AddInvestmentDTO addInvestmentDTO;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        addInvestmentDTO = new AddInvestmentDTO(
                1L,
                1001L,
                "Ação",
                "PETR4",
                50.0,
                27.50,
                30.10,
                LocalDate.of(2025, 6, 3)
        );
    }

    @Test
    @DisplayName("Should import CSV successfully")
    void importCSV_success(@TempDir Path tempDir) throws Exception {
        // Arrange
        Path filePath = tempDir.resolve("investments.csv");
        Files.write(filePath, List.of(
                "tipo,ticker,quantidade,preco,data",
                "Ação,PETR4,10,25.0,2024-05-20"
        ));

        // Criar dois objetos distintos para simular os saves com status diferentes
        StatementImport inProgressStmt = new StatementImport();
        inProgressStmt.setId(1L);
        inProgressStmt.setStatus("IN_PROGRESS");
        inProgressStmt.setUserId(1L);
        inProgressStmt.setFileName("investments.csv");

        StatementImport completedStmt = new StatementImport();
        completedStmt.setId(1L);
        completedStmt.setStatus("COMPLETED");
        completedStmt.setUserId(1L);
        completedStmt.setFileName("investments.csv");

        // Simular os dois saves com objetos diferentes
        when(statementImportRepository.save(any()))
                .thenReturn(inProgressStmt)  // primeiro save
                .thenReturn(completedStmt);  // segundo save

        when(modelMapper.map(any(Investment.class), eq(AddInvestmentDTO.class)))
                .thenReturn(addInvestmentDTO);

        // Act
        statementImportService.importCSV(1L, filePath.toString());

        // Assert
        verify(statementImportRepository, times(2)).save(stmtCaptor.capture());
        List<StatementImport> saves = stmtCaptor.getAllValues();

        assertEquals("IN_PROGRESS", saves.get(0).getStatus());
        assertEquals("COMPLETED", saves.get(1).getStatus());

        verify(investmentService).addInvestment(any(AddInvestmentDTO.class));
    }

    @Test
    @DisplayName("Should import XLSX successfully")
    void importXLSX_success(@TempDir Path tempDir) throws Exception {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Investments");

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("tipo");
        header.createCell(1).setCellValue("ticker");
        header.createCell(2).setCellValue("quantidade");
        header.createCell(3).setCellValue("preco");
        header.createCell(4).setCellValue("data");

        Row row = sheet.createRow(1);
        row.createCell(0).setCellValue("Ação");
        row.createCell(1).setCellValue("PETR4");
        row.createCell(2).setCellValue(10.0);
        row.createCell(3).setCellValue(25.0);

        Cell dateCell = row.createCell(4);
        LocalDate date = LocalDate.of(2024, 5, 20);
        dateCell.setCellValue(java.sql.Date.valueOf(date));

        CreationHelper creationHelper = workbook.getCreationHelper();
        CellStyle dateStyle = workbook.createCellStyle();
        dateStyle.setDataFormat(creationHelper.createDataFormat().getFormat("yyyy-MM-dd"));
        dateCell.setCellStyle(dateStyle);

        Path filePath = tempDir.resolve("investments.xlsx");
        try (FileOutputStream fos = new FileOutputStream(filePath.toFile())) {
            workbook.write(fos);
        }
        workbook.close();

        when(statementImportRepository.save(any())).thenAnswer(i -> {
            StatementImport stmt = i.getArgument(0);
            stmt.setId(1L);
            return stmt;
        });

        when(modelMapper.map(any(Investment.class), eq(AddInvestmentDTO.class)))
                .thenReturn(addInvestmentDTO);

        statementImportService.importXLSX(1L, filePath.toString());

        verify(statementImportRepository, atLeastOnce()).save(argThat(stmt ->
                Objects.equals(stmt.getUserId(), 1L) &&
                        stmt.getFileName().equals("investments.xlsx") &&
                        List.of("IN_PROGRESS", "COMPLETED").contains(stmt.getStatus())
        ));
        verify(investmentService, times(1)).addInvestment(any(AddInvestmentDTO.class));
    }

    @Test
    @DisplayName("Should throw exception when validating invalid data")
    void validateData_invalid() {
        List<Investment> invalidList = List.of(new Investment(
                null,
                null,
                "Ação",
                "PETR4",
                0.0,
                -10.0,
                null,
                null
        ));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> statementImportService.validateData(invalidList));

        assertTrue(ex.getMessage().contains("Invalid quantity"));
    }
}