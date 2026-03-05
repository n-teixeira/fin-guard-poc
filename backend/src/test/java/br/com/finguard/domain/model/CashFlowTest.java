package br.com.finguard.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class CashFlowTest {

    @Test
    void shouldCreateEmptyCashFlow() {
        var cashFlow = CashFlow.empty("user-1");

        assertEquals("user-1", cashFlow.userId());
        assertEquals(BigDecimal.ZERO, cashFlow.balance());
        assertEquals(BigDecimal.ZERO, cashFlow.totalCredits());
        assertEquals(BigDecimal.ZERO, cashFlow.totalDebits());
    }

    @Test
    void shouldApplyCreditTransaction() {
        var cashFlow = CashFlow.empty("user-1");
        var transaction = Transaction.create(
                "user-1",
                new BigDecimal("100.50"),
                TransactionType.PIX_IN,
                "João",
                "Pix recebido",
                "com.banco.app"
        );

        var updated = cashFlow.applyTransaction(transaction);

        assertEquals(new BigDecimal("100.50"), updated.balance());
        assertEquals(new BigDecimal("100.50"), updated.totalCredits());
        assertEquals(BigDecimal.ZERO, updated.totalDebits());
    }

    @Test
    void shouldApplyDebitTransaction() {
        var cashFlow = CashFlow.empty("user-1");
        var transaction = Transaction.create(
                "user-1",
                new BigDecimal("50.00"),
                TransactionType.PAYMENT,
                "Supermercado",
                "Pagamento aprovado",
                "com.banco.app"
        );

        var updated = cashFlow.applyTransaction(transaction);

        assertEquals(new BigDecimal("-50.00"), updated.balance());
        assertEquals(BigDecimal.ZERO, updated.totalCredits());
        assertEquals(new BigDecimal("50.00"), updated.totalDebits());
    }
}
