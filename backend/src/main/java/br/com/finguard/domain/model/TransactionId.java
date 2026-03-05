package br.com.finguard.domain.model;

import java.util.UUID;

/**
 * Value Object - Identificador único de transação.
 */
public record TransactionId(UUID value) {

    public static TransactionId generate() {
        return new TransactionId(UUID.randomUUID());
    }

    public static TransactionId from(String value) {
        return new TransactionId(UUID.fromString(value));
    }
}
