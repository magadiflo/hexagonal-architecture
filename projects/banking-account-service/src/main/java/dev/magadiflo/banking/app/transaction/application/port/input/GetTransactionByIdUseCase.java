package dev.magadiflo.banking.app.transaction.application.port.input;

import dev.magadiflo.banking.app.transaction.application.dto.response.TransactionResponse;

public interface GetTransactionByIdUseCase {
    TransactionResponse execute(String referenceNumber);
}
