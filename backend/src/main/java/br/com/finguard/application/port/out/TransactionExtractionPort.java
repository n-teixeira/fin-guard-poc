package br.com.finguard.application.port.out;

import br.com.finguard.domain.model.ExtractedTransactionData;
import br.com.finguard.domain.model.NotificationPayload;

import java.util.Optional;

/**
 * Output Port - Extração de transação via regex.
 */
public interface TransactionExtractionPort {

    Optional<ExtractedTransactionData> extract(NotificationPayload payload);
}
