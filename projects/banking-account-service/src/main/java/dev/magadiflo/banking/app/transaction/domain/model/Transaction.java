package dev.magadiflo.banking.app.transaction.domain.model;

import dev.magadiflo.banking.app.account.domain.model.vo.Money;
import dev.magadiflo.banking.app.transaction.domain.model.enums.TransactionStatus;
import dev.magadiflo.banking.app.transaction.domain.model.enums.TransactionType;
import dev.magadiflo.banking.app.transaction.domain.model.vo.ReferenceNumber;
import dev.magadiflo.banking.app.transaction.domain.model.vo.TransactionId;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Transaction {

    private TransactionId id;
    private ReferenceNumber referenceNumber;
    private Long accountId;          // Referencia a la cuenta por su ID interno
    private TransactionType type;
    private Money amount;
    private String description;
    private TransactionStatus status;
    private LocalDateTime createdAt;

    // FACTORY METHODS
    // =========================================================

    /**
     * Registra una nueva transacción exitosa (COMPLETED).
     * Se usa cuando la operación (depósito, retiro, etc.)
     * se completó satisfactoriamente.
     */
    public static Transaction registerCompleted(String referenceNumber,
                                                Long accountId,
                                                TransactionType type,
                                                Money amount,
                                                String description) {

        Transaction transaction = new Transaction();
        transaction.referenceNumber = new ReferenceNumber(referenceNumber);
        transaction.accountId = accountId;
        transaction.type = type;
        transaction.amount = amount;
        transaction.description = description;
        transaction.status = TransactionStatus.COMPLETED;

        return transaction;
    }

    /**
     * Registra una transacción fallida (FAILED).
     * Se usa cuando la operación fue rechazada por una regla
     * de negocio (saldo insuficiente, cuenta bloqueada, etc.).
     * Registrar los intentos fallidos es una práctica bancaria
     * estándar para auditoría y detección de fraude.
     */
    public static Transaction registerFailed(String referenceNumber,
                                             Long accountId,
                                             TransactionType type,
                                             Money amount,
                                             String failureReason) {

        Transaction transaction = new Transaction();
        transaction.referenceNumber = new ReferenceNumber(referenceNumber);
        transaction.accountId = accountId;
        transaction.type = type;
        transaction.amount = amount;
        transaction.description = failureReason;
        transaction.status = TransactionStatus.FAILED;

        return transaction;
    }

    /**
     * Reconstituye una transacción existente desde la base de datos.
     * Usado por el mapper de infraestructura.
     */
    public static Transaction reconstitute(Long id,
                                           String referenceNumber,
                                           Long accountId,
                                           TransactionType type,
                                           Money amount,
                                           String description,
                                           TransactionStatus status,
                                           LocalDateTime createdAt) {

        Transaction transaction = new Transaction();
        transaction.id = new TransactionId(id);
        transaction.referenceNumber = new ReferenceNumber(referenceNumber);
        transaction.accountId = accountId;
        transaction.type = type;
        transaction.amount = amount;
        transaction.description = description;
        transaction.status = status;
        transaction.createdAt = createdAt;

        return transaction;
    }

    // CONSULTAS — no hay métodos de modificación porque
    // las transacciones son inmutables por diseño
    // =========================================================
    public boolean isCompleted() {
        return this.status == TransactionStatus.COMPLETED;
    }

    public boolean isFailed() {
        return this.status == TransactionStatus.FAILED;
    }
}
