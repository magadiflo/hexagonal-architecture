package dev.magadiflo.banking.app.customer.application.dto.command;

import dev.magadiflo.banking.app.customer.domain.model.enums.DocumentType;

public record CreateCustomerCommand(String documentNumber,
                                    DocumentType documentType,
                                    String firstName,
                                    String lastName,
                                    String email,
                                    String phone) {
}
