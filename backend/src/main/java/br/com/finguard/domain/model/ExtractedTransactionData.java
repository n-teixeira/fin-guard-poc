package br.com.finguard.domain.model;

import java.math.BigDecimal;

/**
 * Value Object - Dados extraídos da notificação (sem userId).
 */
public record ExtractedTransactionData(
        BigDecimal amount,
        TransactionType type,
        String origin,
        String rawNotification,
        String bankIdentifier
) {
}
