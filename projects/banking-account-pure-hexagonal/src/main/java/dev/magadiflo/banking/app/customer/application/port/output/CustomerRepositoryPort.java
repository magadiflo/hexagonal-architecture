package dev.magadiflo.banking.app.customer.application.port.output;

import dev.magadiflo.banking.app.customer.domain.model.Customer;

import java.util.List;
import java.util.Optional;

public interface CustomerRepositoryPort {
    List<Customer> findAll();

    Optional<Customer> findById(Long customerId);

    Optional<Customer> findByCustomerCode(String customerCode);

    Customer save(Customer customer);

    boolean existsByDocumentNumber(String documentNumber);

    boolean existsByEmail(String email);
}
