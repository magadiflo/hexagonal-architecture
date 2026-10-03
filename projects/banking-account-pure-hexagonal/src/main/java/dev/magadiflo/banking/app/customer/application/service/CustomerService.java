package dev.magadiflo.banking.app.customer.application.service;

import dev.magadiflo.banking.app.customer.application.dto.command.CreateCustomerCommand;
import dev.magadiflo.banking.app.customer.application.dto.command.UpdateCustomerCommand;
import dev.magadiflo.banking.app.customer.application.dto.response.CustomerResponse;
import dev.magadiflo.banking.app.customer.application.helper.CustomerApplicationHelper;
import dev.magadiflo.banking.app.customer.application.mapper.CustomerApplicationMapper;
import dev.magadiflo.banking.app.customer.application.port.input.CreateCustomerUseCase;
import dev.magadiflo.banking.app.customer.application.port.input.DeleteCustomerUseCase;
import dev.magadiflo.banking.app.customer.application.port.input.GetAllCustomersUseCase;
import dev.magadiflo.banking.app.customer.application.port.input.GetCustomerByCodeUseCase;
import dev.magadiflo.banking.app.customer.application.port.input.UpdateCustomerUseCase;
import dev.magadiflo.banking.app.customer.application.port.output.CustomerRepositoryPort;
import dev.magadiflo.banking.app.customer.domain.exception.CustomerAlreadyExistsException;
import dev.magadiflo.banking.app.customer.domain.exception.CustomerNotFoundException;
import dev.magadiflo.banking.app.customer.domain.model.Customer;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class CustomerService implements GetAllCustomersUseCase, GetCustomerByCodeUseCase, CreateCustomerUseCase,
        UpdateCustomerUseCase, DeleteCustomerUseCase {

    private final CustomerRepositoryPort customerRepositoryPort;
    private final CustomerApplicationMapper customerApplicationMapper;
    private final CustomerApplicationHelper customerApplicationHelper;

    @Override
    public List<CustomerResponse> execute() {
        return this.customerRepositoryPort.findAll().stream()
                .map(this.customerApplicationMapper::toResponse)
                .toList();
    }

    @Override
    public CustomerResponse execute(String customerCode) {
        Customer customer = this.findByCodeOrThrow(customerCode);
        return this.customerApplicationMapper.toResponse(customer);
    }

    @Override
    public CustomerResponse execute(CreateCustomerCommand command) {
        if (this.customerRepositoryPort.existsByDocumentNumber(command.documentNumber())) {
            throw new CustomerAlreadyExistsException("número de documento", command.documentNumber());
        }

        if (this.customerRepositoryPort.existsByEmail(command.email())) {
            throw new CustomerAlreadyExistsException("email", command.email());
        }

        String customerCode = this.customerApplicationHelper.generateCustomerCode();
        Customer customer = Customer.create(
                customerCode,
                command.documentNumber(),
                command.documentType(),
                command.firstName(),
                command.lastName(),
                command.email(),
                command.phone()
        );
        Customer savedCustomer = this.customerRepositoryPort.save(customer);
        return this.customerApplicationMapper.toResponse(savedCustomer);
    }

    @Override
    public CustomerResponse execute(String customerCode, UpdateCustomerCommand command) {
        Customer customer = this.findByCodeOrThrow(customerCode);
        customer.validateIsOperational();
        customer.updatePersonalInfo(command.firstName(), command.lastName(), command.phone());
        Customer updatedCustomer = this.customerRepositoryPort.save(customer);
        return this.customerApplicationMapper.toResponse(updatedCustomer);
    }

    @Override
    public void executeDelete(String customerCode) {
        Customer customer = this.findByCodeOrThrow(customerCode);
        customer.deactivate();
        this.customerRepositoryPort.save(customer);
    }

    private Customer findByCodeOrThrow(String customerCode) {
        return this.customerRepositoryPort.findByCustomerCode(customerCode)
                .orElseThrow(() -> new CustomerNotFoundException(customerCode));
    }
}
