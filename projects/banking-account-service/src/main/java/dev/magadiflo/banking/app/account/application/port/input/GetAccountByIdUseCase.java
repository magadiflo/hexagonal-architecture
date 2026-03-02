package dev.magadiflo.banking.app.account.application.port.input;

import dev.magadiflo.banking.app.account.application.dto.response.AccountResponse;

public interface GetAccountByIdUseCase {
    AccountResponse execute(String accountNumber);
}
