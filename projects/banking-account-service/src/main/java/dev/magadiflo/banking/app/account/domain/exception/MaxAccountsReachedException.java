package dev.magadiflo.banking.app.account.domain.exception;

public class MaxAccountsReachedException extends RuntimeException {
    public MaxAccountsReachedException(String customerCode, int maxAccounts) {
        super("El cliente %s ya tiene el máximo de %d cuentas activas permitidas".formatted(customerCode, maxAccounts));
    }
}
