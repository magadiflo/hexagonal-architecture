package dev.magadiflo.banking.app.account.domain.exception;

public class InvalidAmountException extends RuntimeException {
    public InvalidAmountException() {
        super("El monto de la operación debe ser mayor a cero");
    }
}
