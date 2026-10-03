package dev.magadiflo.banking.app.customer.domain.model.vo;

import java.util.Objects;

public record CustomerId(Long value) {
    public CustomerId {
        if (Objects.isNull(value) || value <= 0) {
            throw new IllegalArgumentException("El ID del cliente debe ser un número positivo");
        }
    }
}
