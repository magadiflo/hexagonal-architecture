package dev.magadiflo.banking.app.account.infrastructure.adapter.output.persistence.mapper;

import dev.magadiflo.banking.app.account.domain.model.Account;
import dev.magadiflo.banking.app.account.domain.model.vo.Money;
import dev.magadiflo.banking.app.account.infrastructure.adapter.output.persistence.entity.AccountEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AccountInfraMapper {

    // Convierte dominio → entidad JPA
    // Money se desensambla en dos campos separados: balance y currency
    @Mapping(target = "id", expression = "java(account.getId() != null ? account.getId().value() : null)")
    @Mapping(target = "accountNumber", expression = "java(account.getAccountNumber().value())")
    @Mapping(target = "balance", expression = "java(account.getBalance().amount())")
    @Mapping(target = "currency", expression = "java(account.getBalance().currency())")
    AccountEntity toEntity(Account account);

    // Convierte entidad JPA → dominio llamando explícitamente a reconstitute()
    // Money se ensambla desde los campos separados balance y currency
    default Account toDomain(AccountEntity entity) {
        return Account.reconstitute(
                entity.getId(),
                entity.getAccountNumber(),
                entity.getCustomerId(),
                entity.getAccountType(),
                Money.of(entity.getBalance(), entity.getCurrency()),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
