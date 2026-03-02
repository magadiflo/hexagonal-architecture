package dev.magadiflo.banking.app.account.application.port.input;

import dev.magadiflo.banking.app.account.application.dto.response.AccountResponse;

import java.util.List;

public interface GetAccountsByCustomerUseCase {
    List<AccountResponse> executeAccountsByCustomer(String customerCode);
}
