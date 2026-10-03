package dev.magadiflo.banking.app.customer.application.port.input;

import dev.magadiflo.banking.app.customer.application.dto.command.CreateCustomerCommand;
import dev.magadiflo.banking.app.customer.application.dto.response.CustomerResponse;

public interface CreateCustomerUseCase {
    CustomerResponse execute(CreateCustomerCommand command);
}
