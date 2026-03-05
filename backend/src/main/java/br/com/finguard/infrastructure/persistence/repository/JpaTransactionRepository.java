package br.com.finguard.infrastructure.persistence.repository;

import br.com.finguard.infrastructure.persistence.entity.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

interface JpaTransactionRepository extends JpaRepository<TransactionEntity, String> {

    List<TransactionEntity> findAllByUserIdOrderByOccurredAtDesc(String userId);
}
