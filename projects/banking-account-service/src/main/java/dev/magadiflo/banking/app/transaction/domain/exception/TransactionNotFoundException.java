package dev.magadiflo.banking.app.transaction.domain.exception;

public class TransactionNotFoundException extends RuntimeException {
    public TransactionNotFoundException(String referenceNumber) {
        super("No se encontró la transacción con número de referencia: %s".formatted(referenceNumber));
    }
}
