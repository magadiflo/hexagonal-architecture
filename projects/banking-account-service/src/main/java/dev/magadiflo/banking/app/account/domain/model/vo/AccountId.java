package dev.magadiflo.banking.app.account.domain.model.vo;

import java.util.Objects;

public record AccountId(Long value) {

    public AccountId {
        if (Objects.isNull(value) || value <= 0) {
            throw new IllegalArgumentException("El ID de la cuenta debe ser un número positivo");
        }
    }
}
