package dev.magadiflo.banking.app.customer.domain.exception;

public class CustomerInactiveException extends RuntimeException {
    public CustomerInactiveException(String customerCode) {
        super("El cliente con código %s se encuentra inactivo y no puede realizar operaciones".formatted(customerCode));
    }
}
