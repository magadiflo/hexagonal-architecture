package dev.magadiflo.banking.app.customer.application.dto.response;

import dev.magadiflo.banking.app.customer.domain.model.enums.CustomerStatus;
import dev.magadiflo.banking.app.customer.domain.model.enums.DocumentType;

import java.time.LocalDateTime;

public record CustomerResponse(String customerCode,
                               String documentNumber,
                               DocumentType documentType,
                               String firstName,
                               String lastName,
                               String fullName,
                               String email,
                               String phone,
                               CustomerStatus status,
                               LocalDateTime createdAt,
                               LocalDateTime updatedAt) {
}
