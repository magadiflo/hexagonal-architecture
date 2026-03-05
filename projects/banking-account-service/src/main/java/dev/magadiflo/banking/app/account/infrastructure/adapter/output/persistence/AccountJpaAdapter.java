package dev.magadiflo.banking.app.account.infrastructure.adapter.output.persistence;

import dev.magadiflo.banking.app.account.application.port.output.AccountRepositoryPort;
import dev.magadiflo.banking.app.account.domain.model.Account;
import dev.magadiflo.banking.app.account.domain.model.enums.AccountStatus;
import dev.magadiflo.banking.app.account.infrastructure.adapter.output.persistence.entity.AccountEntity;
import dev.magadiflo.banking.app.account.infrastructure.adapter.output.persistence.mapper.AccountInfraMapper;
import dev.magadiflo.banking.app.account.infrastructure.adapter.output.persistence.repository.AccountJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class AccountJpaAdapter implements AccountRepositoryPort {

    private final AccountJpaRepository accountJpaRepository;
    private final AccountInfraMapper accountInfraMapper;

    @Override
    public List<Account> findByCustomerId(Long customerId) {
        return this.accountJpaRepository.findByCustomerId(customerId)
                .stream()
                .map(this.accountInfraMapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Account> findById(Long accountId) {
        return this.accountJpaRepository.findById(accountId)
                .map(this.accountInfraMapper::toDomain);
    }

    @Override
    public Optional<Account> findByAccountNumber(String accountNumber) {
        return this.accountJpaRepository.findByAccountNumber(accountNumber)
                .map(this.accountInfraMapper::toDomain);
    }

    @Override
    public Account save(Account account) {
        AccountEntity entity = this.accountInfraMapper.toEntity(account);
        AccountEntity savedEntity = this.accountJpaRepository.save(entity);
        return this.accountInfraMapper.toDomain(savedEntity);
    }

    @Override
    public int countActiveAccountsByCustomerId(Long customerId) {
        return this.accountJpaRepository.countByCustomerIdAndStatus(customerId, AccountStatus.ACTIVE);
    }
}
