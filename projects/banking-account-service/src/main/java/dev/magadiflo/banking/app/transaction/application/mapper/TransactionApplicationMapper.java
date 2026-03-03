package dev.magadiflo.banking.app.transaction.application.mapper;

import dev.magadiflo.banking.app.account.domain.model.Account;
import dev.magadiflo.banking.app.transaction.application.dto.response.TransactionResponse;
import dev.magadiflo.banking.app.transaction.domain.model.Transaction;
import org.springframework.stereotype.Component;

@Component
public class TransactionApplicationMapper {
    /**
     * Convierte Transaction de dominio a TransactionResponse.
     * Necesita el Account para incluir el accountNumber en la respuesta,
     * ya que Transaction solo guarda el accountId interno.
     */
    public TransactionResponse toResponse(Transaction transaction, Account account) {
        return new TransactionResponse(
                transaction.getReferenceNumber().value(),
                account.getAccountNumber().value(),
                transaction.getType(),
                transaction.getAmount().amount(),
                transaction.getAmount().currency(),
                transaction.getDescription(),
                transaction.getStatus(),
                transaction.getCreatedAt()
        );
    }
}
