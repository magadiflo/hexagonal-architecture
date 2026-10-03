package dev.magadiflo.banking.app.customer.domain.exception;

public class CustomerAlreadyExistsException extends RuntimeException {
    public CustomerAlreadyExistsException(String field, String value) {
        super("Ya existe un cliente registrado con %s: %s".formatted(field, value));
    }
}
