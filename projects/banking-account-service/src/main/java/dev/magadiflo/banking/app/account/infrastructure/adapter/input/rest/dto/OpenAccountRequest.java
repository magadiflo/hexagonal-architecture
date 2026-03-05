package dev.magadiflo.banking.app.account.infrastructure.adapter.input.rest.dto;

import dev.magadiflo.banking.app.account.domain.model.enums.AccountType;
import dev.magadiflo.banking.app.account.domain.model.enums.Currency;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record OpenAccountRequest(@NotNull(message = "El tipo de cuenta es obligatorio")
                                 AccountType accountType,

                                 @NotNull(message = "El saldo inicial es obligatorio")
                                 @DecimalMin(value = "0.01", message = "El saldo inicial debe ser mayor a cero")
                                 @Digits(integer = 17, fraction = 2, message = "El saldo debe tener máximo 2 decimales")
                                 BigDecimal initialBalance,

                                 @NotNull(message = "La moneda es obligatoria")
                                 Currency currency) {
}
