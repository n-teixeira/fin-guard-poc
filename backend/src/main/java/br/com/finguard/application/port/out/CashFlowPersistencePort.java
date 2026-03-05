package br.com.finguard.application.port.out;

import br.com.finguard.domain.model.CashFlow;

import java.util.Optional;

/**
 * Output Port - Persistência do fluxo de caixa.
 */
public interface CashFlowPersistencePort {

    CashFlow save(CashFlow cashFlow);

    Optional<CashFlow> findByUserId(String userId);
}
