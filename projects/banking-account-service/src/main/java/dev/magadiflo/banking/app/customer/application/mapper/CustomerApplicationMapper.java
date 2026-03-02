package dev.magadiflo.banking.app.customer.application.mapper;

import dev.magadiflo.banking.app.customer.application.dto.response.CustomerResponse;
import dev.magadiflo.banking.app.customer.domain.model.Customer;
import org.springframework.stereotype.Component;

@Component
public class CustomerApplicationMapper {
    /**
     * Convierte una entidad de dominio Customer a CustomerResponse.
     * Este mapper manual vive en la capa de aplicación porque
     * la transformación es entre dominio y respuesta de aplicación,
     * no involucra infraestructura.
     */
    public CustomerResponse toResponse(Customer customer) {
        return new CustomerResponse(
                customer.getCustomerCode().value(),
                customer.getDocumentNumber().value(),
                customer.getDocumentType(),
                customer.getFirstName(),
                customer.getLastName(),
                customer.getFullName(),
                customer.getEmail().value(),
                customer.getPhone(),
                customer.getStatus(),
                customer.getCreatedAt(),
                customer.getUpdatedAt()
        );
    }
}
