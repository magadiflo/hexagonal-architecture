package dev.magadiflo.banking.app.customer.application.port.input;

public interface DeleteCustomerUseCase {
    void executeDelete(String customerCode);
}
