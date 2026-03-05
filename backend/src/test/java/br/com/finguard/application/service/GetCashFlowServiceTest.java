package br.com.finguard.application.service;

import br.com.finguard.application.port.out.CashFlowPersistencePort;
import br.com.finguard.domain.model.CashFlow;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetCashFlowServiceTest {

    @Mock
    private CashFlowPersistencePort cashFlowPort;

    @InjectMocks
    private GetCashFlowService service;

    @Test
    void shouldReturnExistingCashFlow() {
        var userId = "user-1";
        var existingCashFlow = new CashFlow(
                userId,
                new BigDecimal("500.00"),
                new BigDecimal("1000.00"),
                new BigDecimal("500.00")
        );

        when(cashFlowPort.findByUserId(userId)).thenReturn(Optional.of(existingCashFlow));

        var result = service.getByUserId(userId);

        assertNotNull(result);
        assertEquals(userId, result.userId());
        assertEquals(new BigDecimal("500.00"), result.balance());
        assertEquals(new BigDecimal("1000.00"), result.totalCredits());
        assertEquals(new BigDecimal("500.00"), result.totalDebits());
        verify(cashFlowPort).findByUserId(userId);
    }

    @Test
    void shouldReturnEmptyCashFlowWhenUserHasNone() {
        var userId = "new-user";

        when(cashFlowPort.findByUserId(userId)).thenReturn(Optional.empty());

        var result = service.getByUserId(userId);

        assertNotNull(result);
        assertEquals(userId, result.userId());
        assertEquals(BigDecimal.ZERO, result.balance());
        assertEquals(BigDecimal.ZERO, result.totalCredits());
        assertEquals(BigDecimal.ZERO, result.totalDebits());
        verify(cashFlowPort).findByUserId(userId);
    }
}
