package dev.magadiflo.banking.app.account.infrastructure.adapter.input.rest.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record WithdrawRequest(@NotNull(message = "El monto es obligatorio")
                              @DecimalMin(value = "0.01", message = "El monto debe ser mayor a cero")
                              @Digits(integer = 17, fraction = 2, message = "El monto debe tener máximo 2 decimales")
                              BigDecimal amount,

                              @Size(max = 255, message = "La descripción no puede superar los 255 caracteres")
                              String description) {
}
