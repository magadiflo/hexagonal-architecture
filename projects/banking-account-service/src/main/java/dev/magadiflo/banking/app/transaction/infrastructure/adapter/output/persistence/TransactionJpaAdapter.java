package dev.magadiflo.banking.app.transaction.infrastructure.adapter.output.persistence;

import dev.magadiflo.banking.app.transaction.application.port.output.TransactionRepositoryPort;
import dev.magadiflo.banking.app.transaction.domain.model.Transaction;
import dev.magadiflo.banking.app.transaction.infrastructure.adapter.output.persistence.entity.TransactionEntity;
import dev.magadiflo.banking.app.transaction.infrastructure.adapter.output.persistence.mapper.TransactionInfraMapper;
import dev.magadiflo.banking.app.transaction.infrastructure.adapter.output.persistence.repository.TransactionJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class TransactionJpaAdapter implements TransactionRepositoryPort {

    private final TransactionJpaRepository transactionJpaRepository;
    private final TransactionInfraMapper transactionInfraMapper;

    @Override
    public List<Transaction> findByAccountIdOrderByCreatedAtDesc(Long accountId) {
        return this.transactionJpaRepository.findByAccountIdOrderByCreatedAtDesc(accountId)
                .stream()
                .map(this.transactionInfraMapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Transaction> findByReferenceNumber(String referenceNumber) {
        return this.transactionJpaRepository.findByReferenceNumber(referenceNumber)
                .map(this.transactionInfraMapper::toDomain);
    }

    @Override
    public Transaction save(Transaction transaction) {
        TransactionEntity entity = this.transactionInfraMapper.toEntity(transaction);
        TransactionEntity savedEntity = this.transactionJpaRepository.save(entity);
        return this.transactionInfraMapper.toDomain(savedEntity);
    }
}
