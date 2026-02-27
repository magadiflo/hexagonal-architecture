package dev.magadiflo.banking.app.account.domain.exception;

import java.math.BigDecimal;

public class InsufficientFundsException extends RuntimeException {
    public InsufficientFundsException(BigDecimal balance, BigDecimal amount) {
        super("Salgo insuficiente. Saldo disponible: %.2f, Monto solicitado: %.2f".formatted(balance, amount));
    }
}
