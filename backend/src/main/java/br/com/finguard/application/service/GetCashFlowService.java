package br.com.finguard.application.service;

import br.com.finguard.application.port.in.GetCashFlowUseCase;
import br.com.finguard.application.port.out.CashFlowPersistencePort;
import br.com.finguard.domain.model.CashFlow;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class GetCashFlowService implements GetCashFlowUseCase {

    private final CashFlowPersistencePort cashFlowPort;

    @Override
    public CashFlow getByUserId(String userId) {
        return cashFlowPort.findByUserId(userId)
                .orElse(CashFlow.empty(userId));
    }
}
