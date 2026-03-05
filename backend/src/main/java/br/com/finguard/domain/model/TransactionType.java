package br.com.finguard.domain.model;

/**
 * Value Object - Tipo da transação financeira.
 */
public enum TransactionType {
    CREDIT,
    DEBIT,
    PIX_IN,
    PIX_OUT,
    TRANSFER_IN,
    TRANSFER_OUT,
    PAYMENT,
    UNKNOWN
}
