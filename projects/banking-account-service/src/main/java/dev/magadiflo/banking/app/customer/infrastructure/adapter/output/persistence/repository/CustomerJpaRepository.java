package dev.magadiflo.banking.app.customer.infrastructure.adapter.output.persistence.repository;

import dev.magadiflo.banking.app.customer.infrastructure.adapter.output.persistence.entity.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerJpaRepository extends JpaRepository<CustomerEntity, Long> {

    Optional<CustomerEntity> findByCustomerCode(String customerCode);

    boolean existsByDocumentNumber(String documentNumber);

    boolean existsByEmail(String email);
}
