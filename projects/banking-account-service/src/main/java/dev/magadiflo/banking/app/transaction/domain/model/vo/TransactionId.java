package dev.magadiflo.banking.app.transaction.domain.model.vo;

import java.util.Objects;

public record TransactionId(Long value) {

    public TransactionId {
        if (Objects.isNull(value) || value <= 0) {
            throw new IllegalArgumentException("El ID de la transacción debe ser un número positivo");
        }
    }
}
