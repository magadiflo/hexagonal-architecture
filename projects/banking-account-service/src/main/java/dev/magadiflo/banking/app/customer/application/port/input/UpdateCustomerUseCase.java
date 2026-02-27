package dev.magadiflo.banking.app.customer.application.port.input;

import dev.magadiflo.banking.app.customer.application.dto.command.UpdateCustomerCommand;
import dev.magadiflo.banking.app.customer.application.dto.response.CustomerResponse;

public interface UpdateCustomerUseCase {
    CustomerResponse execute(String customerCode, UpdateCustomerCommand command);
}
