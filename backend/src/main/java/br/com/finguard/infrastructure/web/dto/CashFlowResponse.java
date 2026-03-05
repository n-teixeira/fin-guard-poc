package br.com.finguard.infrastructure.web.dto;

import br.com.finguard.domain.model.CashFlow;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class CashFlowResponse {

    private String userId;
    private BigDecimal balance;
    private BigDecimal totalCredits;
    private BigDecimal totalDebits;

    public static CashFlowResponse from(CashFlow c) {
        return CashFlowResponse.builder()
                .userId(c.userId())
                .balance(c.balance())
                .totalCredits(c.totalCredits())
                .totalDebits(c.totalDebits())
                .build();
    }
}
