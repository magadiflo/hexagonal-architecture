package dev.magadiflo.banking.app.transaction.application.port.output;

import dev.magadiflo.banking.app.transaction.domain.model.Transaction;

public interface TransactionRepositoryPort {
    Transaction save(Transaction transaction);
}
