package dev.magadiflo.banking.app.customer.infrastructure.adapter.output.persistence;

import dev.magadiflo.banking.app.customer.application.port.output.CustomerRepositoryPort;
import dev.magadiflo.banking.app.customer.domain.model.Customer;
import dev.magadiflo.banking.app.customer.infrastructure.adapter.output.persistence.entity.CustomerEntity;
import dev.magadiflo.banking.app.customer.infrastructure.adapter.output.persistence.mapper.CustomerInfraMapper;
import dev.magadiflo.banking.app.customer.infrastructure.adapter.output.persistence.repository.CustomerJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class CustomerJpaAdapter implements CustomerRepositoryPort {

    private final CustomerJpaRepository customerJpaRepository;
    private final CustomerInfraMapper customerInfraMapper;

    @Override
    public List<Customer> findAll() {
        return this.customerJpaRepository.findAll()
                .stream()
                .map(this.customerInfraMapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Customer> findById(Long customerId) {
        return this.customerJpaRepository.findById(customerId)
                .map(this.customerInfraMapper::toDomain);
    }

    @Override
    public Optional<Customer> findByCustomerCode(String customerCode) {
        return this.customerJpaRepository.findByCustomerCode(customerCode)
                .map(this.customerInfraMapper::toDomain);
    }

    @Override
    public Customer save(Customer customer) {
        CustomerEntity entity = this.customerInfraMapper.toEntity(customer);
        CustomerEntity savedEntity = this.customerJpaRepository.save(entity);
        return this.customerInfraMapper.toDomain(savedEntity);
    }

    @Override
    public boolean existsByDocumentNumber(String documentNumber) {
        return this.customerJpaRepository.existsByDocumentNumber(documentNumber);
    }

    @Override
    public boolean existsByEmail(String email) {
        return this.customerJpaRepository.existsByEmail(email);
    }
}
