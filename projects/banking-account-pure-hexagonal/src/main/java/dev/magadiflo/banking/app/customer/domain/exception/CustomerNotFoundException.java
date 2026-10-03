package dev.magadiflo.banking.app.customer.domain.exception;

public class CustomerNotFoundException extends RuntimeException {
    public CustomerNotFoundException(String customerCode) {
        super("No se encontró el cliente con código: %s".formatted(customerCode));
    }
}
