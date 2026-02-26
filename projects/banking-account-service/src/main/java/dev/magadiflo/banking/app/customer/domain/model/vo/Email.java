package dev.magadiflo.banking.app.customer.domain.model.vo;

import java.util.Objects;

public record Email(String value) {

    private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+\\-]+@[a-zA-Z0-9.\\-]+\\.[a-zA-Z]{2,}$";

    public Email {
        if (Objects.isNull(value) || value.isBlank()) {
            throw new IllegalArgumentException("El email no puede ser vacío");
        }
        if (!value.matches(EMAIL_REGEX)) {
            throw new IllegalArgumentException("El formato de email es inválido: " + value);
        }
    }
}
