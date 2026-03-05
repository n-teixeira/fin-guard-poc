package br.com.finguard.application.port.out;

import br.com.finguard.domain.model.Transaction;

import java.util.List;
import java.util.Optional;

/**
 * Output Port - Persistência de transações.
 */
public interface TransactionPersistencePort {

    Transaction save(Transaction transaction);

    Optional<Transaction> findById(String id);

    List<Transaction> findAllByUserId(String userId);
}
