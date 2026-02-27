package dev.magadiflo.banking.app.customer.application.dto.command;

public record UpdateCustomerCommand(String firstName,
                                    String lastName,
                                    String phone) {
}
