package dev.magadiflo.banking.app.account.application.mapper;

import dev.magadiflo.banking.app.account.application.dto.response.AccountBalanceResponse;
import dev.magadiflo.banking.app.account.application.dto.response.AccountResponse;
import dev.magadiflo.banking.app.account.domain.model.Account;
import dev.magadiflo.banking.app.customer.domain.model.Customer;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class AccountApplicationMapper {
    /**
     * Convierte Account de dominio a AccountResponse.
     * Necesita el Customer para incluir el customerCode en la respuesta.
     */
    public AccountResponse toResponse(Account account, Customer customer) {
        return new AccountResponse(
                account.getAccountNumber().value(),
                customer.getCustomerCode().value(),
                account.getAccountType(),
                account.getBalance().amount(),
                account.getBalance().currency(),
                account.getStatus(),
                account.getCreatedAt(),
                account.getUpdatedAt()
        );
    }

    /**
     * Convierte Account de dominio a AccountBalanceResponse.
     * Incluye el tipo de cambio y el equivalente en USD
     * obtenido desde la API externa via ExchangeRatePort.
     */
    public AccountBalanceResponse toBalanceResponse(Account account, BigDecimal balanceInUsd, BigDecimal exchangeRate) {
        return new AccountBalanceResponse(
                account.getAccountNumber().value(),
                account.getBalance().amount(),
                account.getBalance().currency(),
                balanceInUsd,
                exchangeRate
        );
    }
}
