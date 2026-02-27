package dev.magadiflo.banking.app.account.domain.exception;

public class AccountInactiveException extends RuntimeException {
    public AccountInactiveException(String accountNumber) {
        super("La cuenta %s se encuentra inactiva y no puede realizar operaciones".formatted(accountNumber));
    }
}
