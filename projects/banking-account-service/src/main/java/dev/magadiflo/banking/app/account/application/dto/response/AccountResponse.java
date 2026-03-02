package dev.magadiflo.banking.app.account.application.dto.response;

import dev.magadiflo.banking.app.account.domain.model.enums.AccountStatus;
import dev.magadiflo.banking.app.account.domain.model.enums.AccountType;
import dev.magadiflo.banking.app.account.domain.model.enums.Currency;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AccountResponse(String accountNumber,
                              String customerCode,
                              AccountType accountType,
                              BigDecimal balance,
                              Currency currency,
                              AccountStatus status,
                              LocalDateTime createdAt,
                              LocalDateTime updatedAt) {
}
