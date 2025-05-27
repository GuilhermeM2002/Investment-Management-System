package br.com.challenge6.services;

import br.com.challenge6.domain.investment.AddInvestmentDTO;
import br.com.challenge6.domain.investment.GetInvestmentDTO;
import br.com.challenge6.domain.investment.Investment;
import br.com.challenge6.repository.InvestmentRepository;
import br.com.challenge6.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InvestmentServiceTest {
    @Mock
    private InvestmentRepository investmentRepository;

    @Mock
    private ModelMapper mapper;

    @InjectMocks
    private InvestmentService investmentService;

    private AddInvestmentDTO addInvestmentDTO;
    private Investment investment;
    private GetInvestmentDTO getInvestmentDTO;
    private Long id;
    private List<Investment> investments;
    private List<GetInvestmentDTO> investmentDTOs;

    @BeforeEach
    void setUp() {
        id = 1L;

        addInvestmentDTO = new AddInvestmentDTO(
                id,
                1001L,
                "Ação",
                "PETR4",
                50.0,
                25.0,
                30.0,
                LocalDate.of(2024, 5, 25)
        );

        investment = new Investment(
                id,
                1001L,
                "Ação",
                "PETR4",
                50.0,
                25.0,
                30.0,
                LocalDate.of(2024, 5, 25)
        );

        getInvestmentDTO = new GetInvestmentDTO(
                id,
                1001L,
                "Ação",
                "PETR4",
                50.0,
                25.0,
                30.0,
                LocalDate.of(2024, 5, 25)
        );

        investments = Arrays.asList(investment, investment, investment);
        investmentDTOs = Arrays.asList(getInvestmentDTO, getInvestmentDTO, getInvestmentDTO);
    }

    @Test
    @DisplayName("Should save investment and return GetInvestmentDTO")
    void addInvestment() {
        when(mapper.map(addInvestmentDTO, Investment.class)).thenReturn(investment);
        when(investmentRepository.save(investment)).thenReturn(investment);
        when(mapper.map(investment, AddInvestmentDTO.class)).thenReturn(addInvestmentDTO);

        var result = investmentService.addInvestment(addInvestmentDTO);

        assertNotNull(result);
        assertEquals(addInvestmentDTO, result);

        verify(mapper).map(addInvestmentDTO, Investment.class);
        verify(investmentRepository).save(investment);
        verify(mapper).map(investment, AddInvestmentDTO.class);
        verifyNoMoreInteractions(investmentRepository, mapper);
    }

    @Test
    @DisplayName("Should return a list of GetInvestmentDTO by user ID")
    void getInvestmentsByUser() {
        when(investmentRepository.findAllByUserId(id)).thenReturn(investments);
        when(mapper.map(any(Investment.class), eq(GetInvestmentDTO.class)))
                .thenReturn(getInvestmentDTO);

        var result = investmentService.getInvestmentsByUser(id);

        assertNotNull(result);
        assertEquals(3, result.size());
        assertTrue(result.stream().allMatch(dto -> dto.equals(getInvestmentDTO)));

        verify(investmentRepository).findAllByUserId(id);
        verify(mapper, times(3)).map(any(Investment.class), eq(GetInvestmentDTO.class));
        verifyNoMoreInteractions(investmentRepository, mapper);
    }

    @Test
    @DisplayName("Should calculate the total portfolio value")
    void calculatePortfolioValue() {
        when(investmentRepository.findAllByUserId(id)).thenReturn(investments);

        // Cada investimento: 50 * 30 = 1500; total = 1500 * 3 = 4500
        double expectedValue = 50.0 * 30.0 * 3;

        var result = investmentService.calculatePortfolioValue(id);

        assertNotNull(result);
        assertEquals(expectedValue, result);

        verify(investmentRepository).findAllByUserId(id);
        verifyNoMoreInteractions(investmentRepository);
    }
}