package dev.magadiflo.banking.app.account.application.port.input;

import dev.magadiflo.banking.app.account.application.dto.command.OpenAccountCommand;
import dev.magadiflo.banking.app.account.application.dto.response.AccountResponse;

public interface OpenAccountUserCase {
    AccountResponse execute(OpenAccountCommand command);
}
