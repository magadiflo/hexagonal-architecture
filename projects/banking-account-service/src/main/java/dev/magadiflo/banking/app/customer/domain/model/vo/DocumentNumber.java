package dev.magadiflo.banking.app.customer.domain.model.vo;

import java.util.Objects;

public record DocumentNumber(String value) {
    public DocumentNumber {
        if (Objects.isNull(value) || value.isBlank()) {
            throw new IllegalArgumentException("El número de documento no puede ser vacío");
        }
        if (value.length() < 8 || value.length() > 20) {
            throw new IllegalArgumentException("El número de documento debe tener entre 8 y 20 caracteres");
        }
    }
}
