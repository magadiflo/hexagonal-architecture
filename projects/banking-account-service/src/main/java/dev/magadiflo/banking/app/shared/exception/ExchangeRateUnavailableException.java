package dev.magadiflo.banking.app.shared.exception;

public class ExchangeRateUnavailableException extends RuntimeException {
    public ExchangeRateUnavailableException(String baseCurrency, String targetCurrency) {
        super("No se pudo obtener el tipo de cambio para el par %s/%s. Intente nuevamente más tarde."
                .formatted(baseCurrency, targetCurrency));
    }
}
