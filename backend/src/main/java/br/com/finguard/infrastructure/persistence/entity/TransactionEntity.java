package br.com.finguard.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "transactions")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class TransactionEntity {

    @Id
    @Column(length = 36)
    private String id;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private String type;

    private String origin;
    @Column(length = 2000)
    private String rawNotification;
    private Instant occurredAt;
    private String bankIdentifier;

    @PrePersist
    void onPersist() {
        if (id == null) {
            id = UUID.randomUUID().toString();
        }
    }
}
