package br.com.finguard.domain.model;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * Aggregate Root - Representa uma transação financeira extraída de notificação bancária.
 */
public record Transaction(
        TransactionId id,
        String userId,
        BigDecimal amount,
        TransactionType type,
        String origin,
        String rawNotification,
        Instant occurredAt,
        String bankIdentifier
) {

    public static Transaction create(String userId, BigDecimal amount, TransactionType type, String origin,
                                    String rawNotification, String bankIdentifier) {
        return new Transaction(
                null,
                userId,
                amount,
                type,
                origin,
                rawNotification,
                Instant.now(),
                bankIdentifier
        );
    }
}
