package dev.magadiflo.banking.app.customer.application.port.input;

public interface DeleteCustomerUseCase {
    void execute(String customerCode);
}
