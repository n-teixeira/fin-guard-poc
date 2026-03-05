package br.com.finguard.domain.repository;

import br.com.finguard.domain.model.CashFlow;

import java.util.Optional;

/**
 * Repository Port - Interface de persistência do fluxo de caixa.
 */
public interface CashFlowRepository {

    CashFlow save(CashFlow cashFlow);

    Optional<CashFlow> findByUserId(String userId);
}
