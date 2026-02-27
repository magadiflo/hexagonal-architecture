package dev.magadiflo.banking.app.account.domain.exception;

public class InvalidAmountException extends RuntimeException {
    public InvalidAmountException(String reason) {
        super("Monto inválido: %s".formatted(reason));
    }
}
