package dev.magadiflo.banking.app.customer.application.port.input;

import dev.magadiflo.banking.app.customer.application.dto.response.CustomerResponse;

public interface GetCustomerByIdUseCase {
    CustomerResponse execute(String customerCode);
}
