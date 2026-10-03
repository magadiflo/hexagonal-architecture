package dev.magadiflo.banking.app.customer.infrastructure.config;

import dev.magadiflo.banking.app.customer.application.dto.command.CreateCustomerCommand;
import dev.magadiflo.banking.app.customer.application.dto.command.UpdateCustomerCommand;
import dev.magadiflo.banking.app.customer.application.dto.response.CustomerResponse;
import dev.magadiflo.banking.app.customer.application.port.input.CreateCustomerUseCase;
import dev.magadiflo.banking.app.customer.application.port.input.DeleteCustomerUseCase;
import dev.magadiflo.banking.app.customer.application.port.input.GetAllCustomersUseCase;
import dev.magadiflo.banking.app.customer.application.port.input.GetCustomerByCodeUseCase;
import dev.magadiflo.banking.app.customer.application.port.input.UpdateCustomerUseCase;
import dev.magadiflo.banking.app.customer.application.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Primary
@Component
@Transactional(readOnly = true)
public class TransactionalCustomerServiceDecorator implements GetAllCustomersUseCase, GetCustomerByCodeUseCase, CreateCustomerUseCase,
        UpdateCustomerUseCase, DeleteCustomerUseCase {

    private final CustomerService delegate;

    @Override
    public List<CustomerResponse> execute() {
        return this.delegate.execute();
    }

    @Override
    public CustomerResponse execute(String customerCode) {
        return this.delegate.execute(customerCode);
    }

    @Override
    @Transactional
    public CustomerResponse execute(CreateCustomerCommand command) {
        return this.delegate.execute(command);
    }

    @Override
    @Transactional
    public CustomerResponse execute(String customerCode, UpdateCustomerCommand command) {
        return this.delegate.execute(customerCode, command);
    }

    @Override
    @Transactional
    public void executeDelete(String customerCode) {
        this.delegate.executeDelete(customerCode);
    }
}
