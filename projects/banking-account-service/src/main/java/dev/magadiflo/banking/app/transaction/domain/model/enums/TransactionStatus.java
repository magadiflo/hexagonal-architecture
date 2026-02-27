package dev.magadiflo.banking.app.transaction.domain.model.enums;

public enum TransactionStatus {
    PENDING,    // La transacción está en proceso
    COMPLETED,  // La transacción se completó exitosamente
    FAILED      // La transacción falló por alguna regla de negocio
}
