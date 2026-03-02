package dev.magadiflo.banking.app.account.application.dto.command;

import java.math.BigDecimal;

public record WithdrawCommand(BigDecimal amount, String description) {
}
