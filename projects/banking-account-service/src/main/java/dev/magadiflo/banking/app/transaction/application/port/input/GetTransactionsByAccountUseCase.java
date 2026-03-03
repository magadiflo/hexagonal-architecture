package dev.magadiflo.banking.app.transaction.application.port.input;

import dev.magadiflo.banking.app.transaction.application.dto.response.TransactionResponse;

import java.util.List;

public interface GetTransactionsByAccountUseCase {
    List<TransactionResponse> execute(String accountNumber);
}
