package dev.magadiflo.banking.app.account.application.dto.command;

import dev.magadiflo.banking.app.account.domain.model.enums.AccountType;
import dev.magadiflo.banking.app.account.domain.model.enums.Currency;

import java.math.BigDecimal;

public record OpenAccountCommand(String customerCode,
                                 AccountType accountType,
                                 BigDecimal initialBalance,
                                 Currency currency) {
}
