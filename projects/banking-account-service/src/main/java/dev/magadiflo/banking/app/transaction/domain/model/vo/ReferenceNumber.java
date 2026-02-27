package dev.magadiflo.banking.app.transaction.domain.model.vo;

import java.util.Objects;

public record ReferenceNumber(String value) {

    public ReferenceNumber {
        if (Objects.isNull(value) || value.isBlank()) {
            throw new IllegalArgumentException("El número de referencia no puede ser vacío");
        }
        if (!value.matches("^TXN-\\d{14}$")) {
            throw new IllegalArgumentException("Formato de número de referencia inválido. Esperado: TXN-XXXXXXXXXXXXXX");
        }
    }
}
