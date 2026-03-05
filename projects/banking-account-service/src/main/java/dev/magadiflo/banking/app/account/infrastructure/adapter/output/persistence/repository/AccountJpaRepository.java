package dev.magadiflo.banking.app.account.infrastructure.adapter.output.persistence.repository;

import dev.magadiflo.banking.app.account.domain.model.enums.AccountStatus;
import dev.magadiflo.banking.app.account.infrastructure.adapter.output.persistence.entity.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AccountJpaRepository extends JpaRepository<AccountEntity, Long> {
    List<AccountEntity> findByCustomerId(Long customerId);

    Optional<AccountEntity> findByAccountNumber(String accountNumber);

    // Contamos solo las cuentas activas para validar el límite de 3
    int countByCustomerIdAndStatus(Long customerId, AccountStatus status);
}
