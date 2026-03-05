package br.com.finguard.domain.repository;

import br.com.finguard.domain.model.Transaction;

import java.util.List;
import java.util.Optional;

/**
 * Repository Port - Interface de persistência de transações.
 */
public interface TransactionRepository {

    Transaction save(Transaction transaction);

    Optional<Transaction> findById(String id);

    List<Transaction> findAllByUserId(String userId);
}
