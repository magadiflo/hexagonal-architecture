package dev.magadiflo.banking.app.transaction.application.service;

import dev.magadiflo.banking.app.account.application.port.output.AccountRepositoryPort;
import dev.magadiflo.banking.app.account.domain.exception.AccountNotFoundException;
import dev.magadiflo.banking.app.account.domain.model.Account;
import dev.magadiflo.banking.app.transaction.application.dto.response.TransactionResponse;
import dev.magadiflo.banking.app.transaction.application.mapper.TransactionApplicationMapper;
import dev.magadiflo.banking.app.transaction.application.port.input.GetTransactionByIdUseCase;
import dev.magadiflo.banking.app.transaction.application.port.input.GetTransactionsByAccountUseCase;
import dev.magadiflo.banking.app.transaction.application.port.output.TransactionRepositoryPort;
import dev.magadiflo.banking.app.transaction.domain.exception.TransactionNotFoundException;
import dev.magadiflo.banking.app.transaction.domain.model.Transaction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class TransactionService implements GetTransactionsByAccountUseCase, GetTransactionByIdUseCase {

    private final TransactionRepositoryPort transactionRepositoryPort;
    private final AccountRepositoryPort accountRepositoryPort;
    private final TransactionApplicationMapper transactionApplicationMapper;

    // GET TRANSACTIONS BY ACCOUNT
    // =========================================================
    @Override
    public List<TransactionResponse> execute(String accountNumber) {
        // 1. Verificar que la cuenta existe
        Account account = this.findAccountByAccountNumberOrThrow(accountNumber);

        // 2. Obtener historial ordenado por fecha descendente
        return this.transactionRepositoryPort.findByAccountIdOrderByCreatedAtDesc(account.getId().value())
                .stream()
                .map(transaction -> this.transactionApplicationMapper.toResponse(transaction, account))
                .toList();
    }

    // GET TRANSACTION BY REFERENCE NUMBER
    // =========================================================
    @Override
    public TransactionResponse executeGetTransaction(String referenceNumber) {
        // 1. Buscar transacción
        Transaction transaction = this.transactionRepositoryPort.findByReferenceNumber(referenceNumber)
                .orElseThrow(() -> new TransactionNotFoundException(referenceNumber));

        // 2. Buscar cuenta para incluir accountNumber en la respuesta
        Account account = this.findAccountByIdOrThrow(transaction.getAccountId());

        return this.transactionApplicationMapper.toResponse(transaction, account);
    }

    // MÉTODOS PRIVADOS DE APOYO
    // =========================================================
    private Account findAccountByAccountNumberOrThrow(String accountNumber) {
        return this.accountRepositoryPort.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException(accountNumber));

    }

    private Account findAccountByIdOrThrow(Long accountId) {
        return this.accountRepositoryPort.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException("ID: " + accountId));
    }
}
