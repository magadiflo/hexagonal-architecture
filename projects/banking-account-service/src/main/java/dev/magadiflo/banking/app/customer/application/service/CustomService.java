package dev.magadiflo.banking.app.customer.application.service;

import dev.magadiflo.banking.app.customer.application.dto.command.CreateCustomerCommand;
import dev.magadiflo.banking.app.customer.application.dto.command.UpdateCustomerCommand;
import dev.magadiflo.banking.app.customer.application.dto.response.CustomerResponse;
import dev.magadiflo.banking.app.customer.application.port.input.CreateCustomerUseCase;
import dev.magadiflo.banking.app.customer.application.port.input.DeleteCustomerUseCase;
import dev.magadiflo.banking.app.customer.application.port.input.GetAllCustomersUseCase;
import dev.magadiflo.banking.app.customer.application.port.input.GetCustomerByIdUseCase;
import dev.magadiflo.banking.app.customer.application.port.input.UpdateCustomerUseCase;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class CustomService implements GetAllCustomersUseCase, GetCustomerByIdUseCase, CreateCustomerUseCase, UpdateCustomerUseCase, DeleteCustomerUseCase {

    @Override
    public List<CustomerResponse> execute() {
        return List.of();
    }

    @Override
    public CustomerResponse execute(String customerCode) {
        return null;
    }

    @Override
    @Transactional
    public CustomerResponse execute(CreateCustomerCommand command) {
        return null;
    }

    @Override
    @Transactional
    public CustomerResponse execute(String customerCode, UpdateCustomerCommand command) {
        return null;
    }

    @Override
    @Transactional
    public void executeDelete(String customerCode) {

    }
}
