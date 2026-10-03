package dev.magadiflo.banking.app.customer.domain.exception;

public class CustomerBlockedException extends RuntimeException {
    public CustomerBlockedException(String customerCode) {
        super("El cliente con código %s se encuentra bloqueado y no puede realizar operaciones".formatted(customerCode));
    }
}
