package br.com.finguard.infrastructure.persistence.repository;

import br.com.finguard.infrastructure.persistence.entity.CashFlowEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

interface JpaCashFlowRepository extends JpaRepository<CashFlowEntity, String> {

    Optional<CashFlowEntity> findByUserId(String userId);
}
