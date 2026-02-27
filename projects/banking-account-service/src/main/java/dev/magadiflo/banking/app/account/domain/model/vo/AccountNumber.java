package dev.magadiflo.banking.app.account.domain.model.vo;

import java.util.Objects;

public record AccountNumber(String value) {

    public AccountNumber {
        if (Objects.isNull(value) || value.isBlank()) {
            throw new IllegalArgumentException("El número de cuenta no puede ser vacío");
        }
        if (!value.matches("^BNK-\\d{14}$")) {
            throw new IllegalArgumentException("Formato de número de cuenta inválido. Esperado: BNK-XXXXXXXXXXXXXX");
        }
    }
}
