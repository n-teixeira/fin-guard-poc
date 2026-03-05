package br.com.finguard.domain.service;

import br.com.finguard.domain.model.NotificationPayload;
import br.com.finguard.domain.model.Transaction;

import java.util.Optional;

/**
 * Domain Service - Port para extração de transação a partir de notificação.
 */
public interface TransactionExtractor {

    Optional<Transaction> extract(NotificationPayload payload);
}
