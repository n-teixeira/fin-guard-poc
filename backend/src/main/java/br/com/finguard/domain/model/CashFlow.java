package br.com.finguard.domain.model;

import java.math.BigDecimal;

/**
 * Aggregate Root - Representa o fluxo de caixa atual do usuário.
 */
public record CashFlow(
        String userId,
        BigDecimal balance,
        BigDecimal totalCredits,
        BigDecimal totalDebits
) {

    public static CashFlow empty(String userId) {
        return new CashFlow(userId, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
    }

    public CashFlow applyTransaction(Transaction transaction) {
        return switch (transaction.type()) {
            case CREDIT, PIX_IN, TRANSFER_IN -> new CashFlow(
                    userId,
                    balance.add(transaction.amount()),
                    totalCredits.add(transaction.amount()),
                    totalDebits
            );
            case DEBIT, PIX_OUT, TRANSFER_OUT, PAYMENT -> new CashFlow(
                    userId,
                    balance.subtract(transaction.amount()),
                    totalCredits,
                    totalDebits.add(transaction.amount())
            );
            case UNKNOWN -> this;
        };
    }
}
