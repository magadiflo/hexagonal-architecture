package dev.magadiflo.banking.app.account.application.port.input;

import dev.magadiflo.banking.app.account.application.dto.command.DepositCommand;
import dev.magadiflo.banking.app.account.application.dto.response.AccountResponse;

public interface DepositUseCase {
    AccountResponse execute(String accountNumber, DepositCommand command);
}
