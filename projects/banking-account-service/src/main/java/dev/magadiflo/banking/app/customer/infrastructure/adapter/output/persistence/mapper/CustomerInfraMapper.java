package dev.magadiflo.banking.app.customer.infrastructure.adapter.output.persistence.mapper;

import dev.magadiflo.banking.app.customer.domain.model.Customer;
import dev.magadiflo.banking.app.customer.infrastructure.adapter.output.persistence.entity.CustomerEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CustomerInfraMapper {

    // Convierte dominio → entidad JPA
    // Extraemos el valor de cada Value Object con .value()
    @Mapping(target = "id", expression = "java(customer.getId() != null ? customer.getId().value() : null)")
    @Mapping(target = "customerCode", expression = "java(customer.getCustomerCode().value())")
    @Mapping(target = "documentNumber", expression = "java(customer.getDocumentNumber().value())")
    @Mapping(target = "email", expression = "java(customer.getEmail().value())")
    CustomerEntity toEntity(Customer customer);

    // Convierte entidad JPA → dominio, llamando explícitamente a reconstitute()
    // Mapeamos cada campo explícitamente porque el dominio usa Value Objects
    // y MapStruct no puede inferir la conversión automáticamente.
    // MapStruct respeta los métodos por default en las interfaces y los incluye en el código
    // generado sin intentar sobreescribirlos. Es la forma estándar de manejar casos donde
    // MapStruct no puede inferir el mapeo automáticamente.
    default Customer toDomain(CustomerEntity customerEntity) {
        return Customer.reconstitute(
                customerEntity.getId(),
                customerEntity.getCustomerCode(),
                customerEntity.getDocumentNumber(),
                customerEntity.getDocumentType(),
                customerEntity.getFirstName(),
                customerEntity.getLastName(),
                customerEntity.getEmail(),
                customerEntity.getPhone(),
                customerEntity.getStatus(),
                customerEntity.getCreatedAt(),
                customerEntity.getUpdatedAt()
        );
    }
}
