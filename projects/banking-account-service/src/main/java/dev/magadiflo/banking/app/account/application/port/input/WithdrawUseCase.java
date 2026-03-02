package dev.magadiflo.banking.app.account.application.port.input;

import dev.magadiflo.banking.app.account.application.dto.command.WithdrawCommand;
import dev.magadiflo.banking.app.account.application.dto.response.AccountResponse;

public interface WithdrawUseCase {
    AccountResponse execute(String accountNumber, WithdrawCommand command);
}
