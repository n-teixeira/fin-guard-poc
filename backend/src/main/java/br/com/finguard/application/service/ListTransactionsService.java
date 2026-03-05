package br.com.finguard.application.service;

import br.com.finguard.application.port.in.ListTransactionsUseCase;
import br.com.finguard.application.port.out.TransactionPersistencePort;
import br.com.finguard.domain.model.Transaction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
class ListTransactionsService implements ListTransactionsUseCase {

    private final TransactionPersistencePort transactionPort;

    @Override
    public List<Transaction> listByUserId(String userId) {
        return transactionPort.findAllByUserId(userId);
    }
}
