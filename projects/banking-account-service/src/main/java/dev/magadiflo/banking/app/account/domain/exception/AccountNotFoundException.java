package dev.magadiflo.banking.app.account.domain.exception;

public class AccountNotFoundException extends RuntimeException {
    public AccountNotFoundException(String accountNumber) {
        super("No se encontró la cuenta con número: %s".formatted(accountNumber));
    }
}
