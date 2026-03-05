package br.com.finguard.application.port.in;

import br.com.finguard.domain.model.Transaction;

/**
 * Use Case - Processamento de notificação bancária interceptada.
 */
public interface ProcessNotificationUseCase {

    Transaction process(String userId, String notificationContent, String packageName);
}
