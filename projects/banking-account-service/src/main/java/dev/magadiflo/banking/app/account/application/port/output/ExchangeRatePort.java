package dev.magadiflo.banking.app.account.application.port.output;

import java.math.BigDecimal;

// Puerto hacia la API externa de tipo de cambio.
// La implementación concreta usa RestClient en infraestructura.
public interface ExchangeRatePort {
    BigDecimal getExchangeRate(String baseCurrency, String targetCurrency);
}
