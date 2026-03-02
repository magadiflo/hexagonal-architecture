package dev.magadiflo.banking.app.customer.application.service;

import dev.magadiflo.banking.app.customer.application.dto.command.CreateCustomerCommand;
import dev.magadiflo.banking.app.customer.application.dto.command.UpdateCustomerCommand;
import dev.magadiflo.banking.app.customer.application.dto.response.CustomerResponse;
import dev.magadiflo.banking.app.customer.application.helper.CustomerApplicationHelper;
import dev.magadiflo.banking.app.customer.application.mapper.CustomerApplicationMapper;
import dev.magadiflo.banking.app.customer.application.port.input.CreateCustomerUseCase;
import dev.magadiflo.banking.app.customer.application.port.input.DeleteCustomerUseCase;
import dev.magadiflo.banking.app.customer.application.port.input.GetAllCustomersUseCase;
import dev.magadiflo.banking.app.customer.application.port.input.GetCustomerByIdUseCase;
import dev.magadiflo.banking.app.customer.application.port.input.UpdateCustomerUseCase;
import dev.magadiflo.banking.app.customer.application.port.output.CustomerRepositoryPort;
import dev.magadiflo.banking.app.customer.domain.exception.CustomerAlreadyExistsException;
import dev.magadiflo.banking.app.customer.domain.exception.CustomerNotFoundException;
import dev.magadiflo.banking.app.customer.domain.model.Customer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class CustomerService implements GetAllCustomersUseCase, GetCustomerByIdUseCase, CreateCustomerUseCase,
        UpdateCustomerUseCase, DeleteCustomerUseCase {

    private final CustomerRepositoryPort customerRepositoryPort;
    private final CustomerApplicationMapper customerApplicationMapper;
    private final CustomerApplicationHelper customerApplicationHelper;

    // GET ALL
    // =========================================================
    @Override
    public List<CustomerResponse> execute() {
        return this.customerRepositoryPort.findAll().stream()
                .map(this.customerApplicationMapper::toResponse)
                .toList();
    }

    // GET BY ID (por customerCode)
    // =========================================================
    @Override
    public CustomerResponse execute(String customerCode) {
        Customer customer = this.findByCodeOrThrow(customerCode);
        return this.customerApplicationMapper.toResponse(customer);
    }

    // CREATE
    // =========================================================
    @Override
    @Transactional
    public CustomerResponse execute(CreateCustomerCommand command) {
        // 1. Validar la unicidad del documento
        if (this.customerRepositoryPort.existsByDocumentNumber(command.documentNumber())) {
            throw new CustomerAlreadyExistsException("número de documento", command.documentNumber());
        }

        // 2. Validar unicidad de email
        if (this.customerRepositoryPort.existsByEmail(command.email())) {
            throw new CustomerAlreadyExistsException("email", command.email());
        }

        // 3. Generar el customerCode - responsabilidad de la aplicación
        String customerCode = this.customerApplicationHelper.generateCustomerCode();

        // 4. Crear la entidad de dominio usando el factory method
        Customer customer = Customer.create(
                customerCode,
                command.documentNumber(),
                command.documentType(),
                command.firstName(),
                command.lastName(),
                command.email(),
                command.phone()
        );

        // 5. Persistir y retornar respuesta
        Customer savedCustomer = this.customerRepositoryPort.save(customer);
        return this.customerApplicationMapper.toResponse(savedCustomer);
    }

    // UPDATE
    // =========================================================
    @Override
    @Transactional
    public CustomerResponse execute(String customerCode, UpdateCustomerCommand command) {
        // 1. Buscar cliente
        Customer customer = this.findByCodeOrThrow(customerCode);

        // 2. Validar que puede operar (no bloqueado ni inactivo)
        customer.validateIsOperational();

        // 3. aplicar cambios a través del method de dominio
        customer.updatePersonalInfo(command.firstName(), command.lastName(), command.phone());

        // 4. Persistir y retornar
        Customer updatedCustomer = this.customerRepositoryPort.save(customer);
        return this.customerApplicationMapper.toResponse(updatedCustomer);
    }

    // DELETE (baja lógica)
    // =========================================================
    @Override
    @Transactional
    public void executeDelete(String customerCode) {
        // 1. Buscar cliente
        Customer customer = this.findByCodeOrThrow(customerCode);

        // 2. Desactivar a través del method de dominio
        // (internamente valida que no esté ya inactivo o bloqueado)
        customer.deactivate();

        // 3. Persistir el cambio de estado
        this.customerRepositoryPort.save(customer);
    }

    // MÉTODOS PRIVADOS DE APOYO
    // =========================================================
    private Customer findByCodeOrThrow(String customerCode) {
        return this.customerRepositoryPort.findByCustomerCode(customerCode)
                .orElseThrow(() -> new CustomerNotFoundException(customerCode));
    }
}
