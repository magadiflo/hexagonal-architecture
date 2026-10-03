package dev.magadiflo.banking.app.customer.domain.model.vo;

import java.util.Objects;

public record CustomerCode(String value) {
    public CustomerCode {
        if (Objects.isNull(value) || value.isBlank()) {
            throw new IllegalArgumentException("El código de cliente no puede ser vacío");
        }
        if (!value.matches("^CUS-\\d{4}-\\d{6}$")) {
            throw new IllegalArgumentException("Formato de código de cliente inválido. Esperado: CUS-YYYY-XXXXXX");
        }
    }
}
