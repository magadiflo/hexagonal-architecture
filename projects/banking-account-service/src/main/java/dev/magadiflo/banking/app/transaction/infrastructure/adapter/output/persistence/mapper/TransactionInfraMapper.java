package dev.magadiflo.banking.app.transaction.infrastructure.adapter.output.persistence.mapper;

import dev.magadiflo.banking.app.account.domain.model.vo.Money;
import dev.magadiflo.banking.app.transaction.domain.model.Transaction;
import dev.magadiflo.banking.app.transaction.infrastructure.adapter.output.persistence.entity.TransactionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TransactionInfraMapper {

    // Convierte dominio → entidad JPA
    // Money se desensambla en amount y currency igual que en AccountInfraMapper
    // type mapea al campo transactionType de la entidad
    @Mapping(target = "id", expression = "java(transaction.getId() != null ? transaction.getId().value() : null)")
    @Mapping(target = "referenceNumber", expression = "java(transaction.getReferenceNumber().value())")
    @Mapping(target = "transactionType", source = "type")
    @Mapping(target = "amount", expression = "java(transaction.getAmount().amount())")
    @Mapping(target = "currency", expression = "java(transaction.getAmount().currency())")
    TransactionEntity toEntity(Transaction transaction);

    // Convierte entidad JPA → dominio llamando explícitamente a reconstitute()
    // Money se ensambla desde los campos separados amount y currency
    default Transaction toDomain(TransactionEntity entity) {
        return Transaction.reconstitute(
                entity.getId(),
                entity.getReferenceNumber(),
                entity.getAccountId(),
                entity.getTransactionType(),
                Money.of(entity.getAmount(), entity.getCurrency()),
                entity.getDescription(),
                entity.getStatus(),
                entity.getCreatedAt()
        );
    }
}
