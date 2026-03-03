package dev.magadiflo.banking.app.transaction.application.port.output;

import dev.magadiflo.banking.app.transaction.domain.model.Transaction;

import java.util.List;
import java.util.Optional;

public interface TransactionRepositoryPort {
    // Persiste una transacción nueva — nunca se actualiza
    Transaction save(Transaction transaction);

    // Busca una transacción por su número de referencia
    Optional<Transaction> findByReferenceNumber(String referenceNumber);

    // Retorna el historial de transacciones de una cuenta
    // ordenado por fecha descendente (más reciente primero)
    List<Transaction> findByAccountIdOrderByCreatedAtDesc(Long accountId);
}
