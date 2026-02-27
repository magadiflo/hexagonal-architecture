package dev.magadiflo.banking.app.account.domain.exception;

public class AccountBlockedException extends RuntimeException {
    public AccountBlockedException(String accountNumber) {
        super("La cuenta %s se encuentra bloqueada y no puede realizar operaciones".formatted(accountNumber));
    }
}
