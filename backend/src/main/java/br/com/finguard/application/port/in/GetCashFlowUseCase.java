package br.com.finguard.application.port.in;

import br.com.finguard.domain.model.CashFlow;

/**
 * Use Case - Obter fluxo de caixa do usuário.
 */
public interface GetCashFlowUseCase {

    CashFlow getByUserId(String userId);
}
