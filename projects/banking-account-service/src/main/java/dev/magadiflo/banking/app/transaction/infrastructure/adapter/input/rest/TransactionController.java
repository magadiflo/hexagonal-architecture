package dev.magadiflo.banking.app.transaction.infrastructure.adapter.input.rest;

import dev.magadiflo.banking.app.transaction.application.dto.response.TransactionResponse;
import dev.magadiflo.banking.app.transaction.application.port.input.GetTransactionByIdUseCase;
import dev.magadiflo.banking.app.transaction.application.port.input.GetTransactionsByAccountUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/api/{version}/transactions", version = "1")
public class TransactionController {

    private final GetTransactionsByAccountUseCase getTransactionsByAccountUseCase;
    private final GetTransactionByIdUseCase getTransactionByIdUseCase;

    @GetMapping(path = "/accounts/{accountNumber}")
    public ResponseEntity<List<TransactionResponse>> getTransactionsByAccount(@PathVariable String accountNumber) {
        return ResponseEntity.ok(this.getTransactionsByAccountUseCase.execute(accountNumber));
    }

    @GetMapping(path = "/{referenceNumber}")
    public ResponseEntity<TransactionResponse> getTransactionByReferenceNumber(@PathVariable String referenceNumber) {
        return ResponseEntity.ok(this.getTransactionByIdUseCase.executeGetTransaction(referenceNumber));
    }
}
