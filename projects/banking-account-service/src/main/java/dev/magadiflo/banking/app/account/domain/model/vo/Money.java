package dev.magadiflo.banking.app.account.domain.model.vo;

import dev.magadiflo.banking.app.account.domain.model.enums.Currency;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

public record Money(BigDecimal amount, Currency currency) {

    public Money {
        if (Objects.isNull(amount)) {
            throw new IllegalArgumentException("El monto no puede ser nulo");
        }
        if (Objects.isNull(currency)) {
            throw new IllegalArgumentException("La moneda no puede ser nula");
        }
        // Normalizamos siempre a 2 decimales para consistencia
        amount = amount.setScale(2, RoundingMode.HALF_UP);
    }

    // Factory method para crear Money de forma más legible
    public static Money of(BigDecimal amount, Currency currency) {
        return new Money(amount, currency);
    }

    public static Money zero(Currency currency) {
        return new Money(BigDecimal.ZERO, currency);
    }

    public boolean isGreaterThan(Money other) {
        this.validateSameCurrency(other);
        return this.amount.compareTo(other.amount) > 0;
    }

    public boolean isGreaterThanOrEqual(Money other) {
        this.validateSameCurrency(other);
        return this.amount.compareTo(other.amount) >= 0;
    }

    public boolean isPositive() {
        return this.amount.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean isZeroOrNegative() {
        return this.amount.compareTo(BigDecimal.ZERO) <= 0;
    }

    public Money add(Money other) {
        this.validateSameCurrency(other);
        return new Money(this.amount.add(other.amount), this.currency);
    }

    public Money subtract(Money other) {
        this.validateSameCurrency(other);
        return new Money(this.amount.subtract(other.amount), this.currency);
    }

    private void validateSameCurrency(Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException("No se pueden operar montos de distintas monedas: %s vs %s"
                    .formatted(this.currency, other.currency));
        }
    }
}
