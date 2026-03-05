package br.com.finguard.application.service;

import br.com.finguard.application.port.in.ProcessNotificationUseCase;
import br.com.finguard.application.port.out.CashFlowPersistencePort;
import br.com.finguard.application.port.out.TransactionExtractionPort;
import br.com.finguard.application.port.out.TransactionPersistencePort;
import br.com.finguard.domain.model.CashFlow;
import br.com.finguard.domain.model.NotificationPayload;
import br.com.finguard.domain.model.Transaction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
class ProcessNotificationService implements ProcessNotificationUseCase {

    private final TransactionExtractionPort extractionPort;
    private final TransactionPersistencePort transactionPort;
    private final CashFlowPersistencePort cashFlowPort;

    @Override
    @Transactional
    public Transaction process(String userId, String notificationContent, String packageName) {
        var payload = new NotificationPayload(notificationContent, packageName);
        var extracted = extractionPort.extract(payload)
                .orElseThrow(() -> new IllegalArgumentException("Não foi possível extrair transação da notificação"));

        var transaction = Transaction.create(
                userId,
                extracted.amount(),
                extracted.type(),
                extracted.origin(),
                extracted.rawNotification(),
                extracted.bankIdentifier()
        );

        var savedTransaction = transactionPort.save(transaction);

        var cashFlow = cashFlowPort.findByUserId(userId)
                .orElse(CashFlow.empty(userId));

        var updatedCashFlow = cashFlow.applyTransaction(savedTransaction);
        cashFlowPort.save(updatedCashFlow);

        return savedTransaction;
    }
}
