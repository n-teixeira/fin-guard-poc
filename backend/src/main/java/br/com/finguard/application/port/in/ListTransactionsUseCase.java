package br.com.finguard.application.port.in;

import br.com.finguard.domain.model.Transaction;

import java.util.List;

/**
 * Use Case - Listar transações do usuário.
 */
public interface ListTransactionsUseCase {

    List<Transaction> listByUserId(String userId);
}
