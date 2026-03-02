package dev.magadiflo.banking.app.account.application.port.input;

import dev.magadiflo.banking.app.account.application.dto.response.AccountBalanceResponse;

public interface GetAccountBalanceUseCase {
    AccountBalanceResponse executeGetBalance(String accountNumber);
}
