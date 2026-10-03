package dev.magadiflo.banking.app.customer.infrastructure.adapter.input.rest;

import dev.magadiflo.banking.app.customer.application.dto.command.CreateCustomerCommand;
import dev.magadiflo.banking.app.customer.application.dto.command.UpdateCustomerCommand;
import dev.magadiflo.banking.app.customer.application.dto.response.CustomerResponse;
import dev.magadiflo.banking.app.customer.application.port.input.CreateCustomerUseCase;
import dev.magadiflo.banking.app.customer.application.port.input.DeleteCustomerUseCase;
import dev.magadiflo.banking.app.customer.application.port.input.GetAllCustomersUseCase;
import dev.magadiflo.banking.app.customer.application.port.input.GetCustomerByCodeUseCase;
import dev.magadiflo.banking.app.customer.application.port.input.UpdateCustomerUseCase;
import dev.magadiflo.banking.app.customer.infrastructure.adapter.input.rest.dto.CreateCustomerRequest;
import dev.magadiflo.banking.app.customer.infrastructure.adapter.input.rest.dto.UpdateCustomerRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/api/{version}/customers", version = "1")
public class CustomerController {

    private final GetAllCustomersUseCase getAllCustomersUseCase;
    private final GetCustomerByCodeUseCase getCustomerByCodeUseCase;
    private final CreateCustomerUseCase createCustomerUseCase;
    private final UpdateCustomerUseCase updateCustomerUseCase;
    private final DeleteCustomerUseCase deleteCustomerUseCase;

    @GetMapping
    public ResponseEntity<List<CustomerResponse>> getAllCustomers() {
        return ResponseEntity.ok(this.getAllCustomersUseCase.execute());
    }

    @GetMapping(path = "/{customerCode}")
    public ResponseEntity<CustomerResponse> getCustomerByCode(@PathVariable String customerCode) {
        return ResponseEntity.ok(this.getCustomerByCodeUseCase.execute(customerCode));
    }

    @PostMapping
    public ResponseEntity<CustomerResponse> createCustomer(@Valid @RequestBody CreateCustomerRequest request) {
        var command = new CreateCustomerCommand(
                request.documentNumber(),
                request.documentType(),
                request.firstName(),
                request.lastName(),
                request.email(),
                request.phone()
        );
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(this.createCustomerUseCase.execute(command));
    }

    @PutMapping(path = "/{customerCode}")
    public ResponseEntity<CustomerResponse> updateCustomer(@PathVariable String customerCode,
                                                           @Valid @RequestBody UpdateCustomerRequest request) {
        var command = new UpdateCustomerCommand(
                request.firstName(),
                request.lastName(),
                request.phone()
        );
        return ResponseEntity.ok(this.updateCustomerUseCase.execute(customerCode, command));
    }

    @DeleteMapping(path = "/{customerCode}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable String customerCode) {
        this.deleteCustomerUseCase.executeDelete(customerCode);
        return ResponseEntity.noContent().build();
    }
}
