package dev.magadiflo.banking.app.transaction.infrastructure.adapter.output.persistence.repository;

import dev.magadiflo.banking.app.transaction.infrastructure.adapter.output.persistence.entity.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TransactionJpaRepository extends JpaRepository<TransactionEntity, Long> {
    // Retorna el historial ordenado por fecha descendente
    // El más reciente primero, como se ve en cualquier app bancaria
    List<TransactionEntity> findByAccountIdOrderByCreatedAtDesc(Long accountId);

    Optional<TransactionEntity> findByReferenceNumber(String referenceNumber);
}
