package br.com.finguard.infrastructure.web.dto;

import br.com.finguard.domain.model.Transaction;
import br.com.finguard.domain.model.TransactionType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;

@Data
@Builder
public class TransactionResponse {

    private String id;
    private String userId;
    private BigDecimal amount;
    private TransactionType type;
    private String origin;
    private Instant occurredAt;
    private String bankIdentifier;

    public static TransactionResponse from(Transaction t) {
        return TransactionResponse.builder()
                .id(t.id() != null ? t.id().value().toString() : null)
                .userId(t.userId())
                .amount(t.amount())
                .type(t.type())
                .origin(t.origin())
                .occurredAt(t.occurredAt())
                .bankIdentifier(t.bankIdentifier())
                .build();
    }
}
