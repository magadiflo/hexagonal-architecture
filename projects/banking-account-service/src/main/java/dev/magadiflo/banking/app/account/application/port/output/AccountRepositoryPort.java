package dev.magadiflo.banking.app.account.application.port.output;

import dev.magadiflo.banking.app.account.domain.model.Account;

import java.util.List;
import java.util.Optional;

public interface AccountRepositoryPort {
    List<Account> findByCustomerId(Long customerId);

    Optional<Account> findById(Long accountId);

    Optional<Account> findByAccountNumber(String accountNumber);

    Account save(Account account);

    int countActiveAccountsByCustomerId(Long customerId);
}
