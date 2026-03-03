package dev.magadiflo.banking.app.transaction.application.dto.response;

import dev.magadiflo.banking.app.account.domain.model.enums.Currency;
import dev.magadiflo.banking.app.transaction.domain.model.enums.TransactionStatus;
import dev.magadiflo.banking.app.transaction.domain.model.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionResponse(String referenceNumber,
                                  String accountNumber,
                                  TransactionType type,
                                  BigDecimal amount,
                                  Currency currency,
                                  String description,
                                  TransactionStatus status,
                                  LocalDateTime createdAt) {
}
