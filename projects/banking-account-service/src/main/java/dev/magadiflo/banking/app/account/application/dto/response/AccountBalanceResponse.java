package dev.magadiflo.banking.app.account.application.dto.response;

import dev.magadiflo.banking.app.account.domain.model.enums.Currency;

import java.math.BigDecimal;

// Respuesta enriquecida que incluye el saldo actual más
// su equivalente en USD consultado desde la API externa.
public record AccountBalanceResponse(String accountNumber,
                                     BigDecimal balance,
                                     Currency currency,
                                     BigDecimal balanceInUsd,
                                     BigDecimal exchangeRate) {
}
