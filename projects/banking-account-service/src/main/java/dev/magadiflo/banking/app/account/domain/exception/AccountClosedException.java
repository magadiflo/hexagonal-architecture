package dev.magadiflo.banking.app.account.domain.exception;

public class AccountClosedException extends RuntimeException {
    public AccountClosedException(String accountNumber) {
        super("La cuenta %s se encuentra cerrada y no puede realizar operaciones".formatted(accountNumber));
    }
}
