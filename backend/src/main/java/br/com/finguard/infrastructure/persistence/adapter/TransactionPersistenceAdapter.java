package br.com.finguard.infrastructure.persistence.adapter;

import br.com.finguard.application.port.out.TransactionPersistencePort;
import br.com.finguard.domain.model.Transaction;
import br.com.finguard.domain.model.TransactionId;
import br.com.finguard.domain.model.TransactionType;
import br.com.finguard.infrastructure.persistence.entity.TransactionEntity;
import br.com.finguard.infrastructure.persistence.repository.JpaTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
class TransactionPersistenceAdapter implements TransactionPersistencePort {

    private final JpaTransactionRepository repository;

    @Override
    public Transaction save(Transaction transaction) {
        var entity = toEntity(transaction);
        var saved = repository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Transaction> findById(String id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Transaction> findAllByUserId(String userId) {
        return repository.findAllByUserIdOrderByOccurredAtDesc(userId)
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    private TransactionEntity toEntity(Transaction t) {
        return TransactionEntity.builder()
                .id(t.id() != null ? t.id().value().toString() : null)
                .userId(t.userId())
                .amount(t.amount())
                .type(t.type().name())
                .origin(t.origin())
                .rawNotification(t.rawNotification())
                .occurredAt(t.occurredAt())
                .bankIdentifier(t.bankIdentifier())
                .build();
    }

    private Transaction toDomain(TransactionEntity e) {
        return new Transaction(
                new TransactionId(UUID.fromString(e.getId())),
                e.getUserId(),
                e.getAmount(),
                TransactionType.valueOf(e.getType()),
                e.getOrigin(),
                e.getRawNotification(),
                e.getOccurredAt(),
                e.getBankIdentifier()
        );
    }
}
