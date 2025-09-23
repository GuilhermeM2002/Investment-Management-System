package br.com.challenge6.domain.user;

import br.com.challenge6.domain.investment.Investment;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class UserInvestmentsDTO {
    private Long userId;
    private List<Investment> investments;
}
