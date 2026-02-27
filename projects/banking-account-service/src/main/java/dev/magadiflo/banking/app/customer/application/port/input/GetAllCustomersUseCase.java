package dev.magadiflo.banking.app.customer.application.port.input;

import dev.magadiflo.banking.app.customer.application.dto.response.CustomerResponse;

import java.util.List;

public interface GetAllCustomersUseCase {
    List<CustomerResponse> execute();
}
