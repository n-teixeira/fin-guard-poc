package br.com.finguard.infrastructure.persistence.adapter;

import br.com.finguard.application.port.out.CashFlowPersistencePort;
import br.com.finguard.domain.model.CashFlow;
import br.com.finguard.infrastructure.persistence.entity.CashFlowEntity;
import br.com.finguard.infrastructure.persistence.repository.JpaCashFlowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
class CashFlowPersistenceAdapter implements CashFlowPersistencePort {

    private final JpaCashFlowRepository repository;

    @Override
    public CashFlow save(CashFlow cashFlow) {
        var entity = toEntity(cashFlow);
        var saved = repository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<CashFlow> findByUserId(String userId) {
        return repository.findByUserId(userId).map(this::toDomain);
    }

    private CashFlowEntity toEntity(CashFlow c) {
        var existing = repository.findByUserId(c.userId());
        var id = existing.map(CashFlowEntity::getId).orElse(UUID.randomUUID().toString());

        return CashFlowEntity.builder()
                .id(id)
                .userId(c.userId())
                .balance(c.balance())
                .totalCredits(c.totalCredits())
                .totalDebits(c.totalDebits())
                .build();
    }

    private CashFlow toDomain(CashFlowEntity e) {
        return new CashFlow(
                e.getUserId(),
                e.getBalance(),
                e.getTotalCredits(),
                e.getTotalDebits()
        );
    }
}
