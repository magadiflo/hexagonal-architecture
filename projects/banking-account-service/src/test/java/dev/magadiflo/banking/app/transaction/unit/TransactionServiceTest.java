package dev.magadiflo.banking.app.transaction.unit;

import dev.magadiflo.banking.app.account.application.port.output.AccountRepositoryPort;
import dev.magadiflo.banking.app.account.domain.exception.AccountNotFoundException;
import dev.magadiflo.banking.app.account.domain.model.Account;
import dev.magadiflo.banking.app.account.domain.model.enums.AccountStatus;
import dev.magadiflo.banking.app.account.domain.model.enums.AccountType;
import dev.magadiflo.banking.app.account.domain.model.enums.Currency;
import dev.magadiflo.banking.app.account.domain.model.vo.Money;
import dev.magadiflo.banking.app.transaction.application.dto.response.TransactionResponse;
import dev.magadiflo.banking.app.transaction.application.mapper.TransactionApplicationMapper;
import dev.magadiflo.banking.app.transaction.application.port.output.TransactionRepositoryPort;
import dev.magadiflo.banking.app.transaction.application.service.TransactionService;
import dev.magadiflo.banking.app.transaction.domain.exception.TransactionNotFoundException;
import dev.magadiflo.banking.app.transaction.domain.model.Transaction;
import dev.magadiflo.banking.app.transaction.domain.model.enums.TransactionStatus;
import dev.magadiflo.banking.app.transaction.domain.model.enums.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@DisplayName("TransactionService — Tests Unitarios")
@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {
    @Mock
    private TransactionRepositoryPort transactionRepositoryPort;
    @Mock
    private AccountRepositoryPort accountRepositoryPort;
    @Mock
    private TransactionApplicationMapper transactionApplicationMapper;

    @InjectMocks
    private TransactionService transactionService;

    private Account activeAccount;
    private Transaction completedTransaction;
    private TransactionResponse expectedResponse;

    @BeforeEach
    void setUp() {
        activeAccount = Account.reconstitute(
                1L, "BNK-20250001234567", 1L, AccountType.SAVINGS,
                Money.of(new BigDecimal("500.00"), Currency.PEN),
                AccountStatus.ACTIVE, LocalDateTime.now(), LocalDateTime.now()
        );

        completedTransaction = Transaction.reconstitute(
                1L, "TXN-20250001234567", 1L,
                TransactionType.DEPOSIT,
                Money.of(new BigDecimal("200.00"), Currency.PEN),
                "Depósito en efectivo",
                TransactionStatus.COMPLETED,
                LocalDateTime.now()
        );

        expectedResponse = new TransactionResponse(
                "TXN-20250001234567",
                "BNK-20250001234567",
                TransactionType.DEPOSIT,
                new BigDecimal("200.00"),
                Currency.PEN,
                "Depósito en efectivo",
                TransactionStatus.COMPLETED,
                LocalDateTime.now()
        );
    }

    // GET TRANSACTIONS BY ACCOUNT
    // =========================================================
    @Nested
    @DisplayName("Obtener transacciones por cuenta")
    class GetTransactionsByAccount {

        @Test
        @DisplayName("Debe retornar la lista de transacciones ordenada por fecha descendente")
        void shouldReturnTransactionsOrderedByDateDesc() {
            // given
            when(accountRepositoryPort.findByAccountNumber("BNK-20250001234567"))
                    .thenReturn(Optional.of(activeAccount));
            when(transactionRepositoryPort.findByAccountIdOrderByCreatedAtDesc(1L))
                    .thenReturn(List.of(completedTransaction));
            when(transactionApplicationMapper.toResponse(any(Transaction.class), any(Account.class)))
                    .thenReturn(expectedResponse);

            // when
            List<TransactionResponse> responses = transactionService.execute("BNK-20250001234567");

            // then
            assertThat(responses)
                    .isNotNull()
                    .hasSize(1)
                    .first()
                    .satisfies(r -> {
                        assertThat(r.referenceNumber()).isEqualTo("TXN-20250001234567");
                        assertThat(r.type()).isEqualTo(TransactionType.DEPOSIT);
                        assertThat(r.status()).isEqualTo(TransactionStatus.COMPLETED);
                    });

            verify(transactionRepositoryPort)
                    .findByAccountIdOrderByCreatedAtDesc(1L);
        }

        @Test
        @DisplayName("Debe retornar lista vacía cuando la cuenta no tiene transacciones")
        void shouldReturnEmptyListWhenNoTransactions() {
            // given
            when(accountRepositoryPort.findByAccountNumber("BNK-20250001234567"))
                    .thenReturn(Optional.of(activeAccount));
            when(transactionRepositoryPort.findByAccountIdOrderByCreatedAtDesc(1L))
                    .thenReturn(List.of());

            // when
            List<TransactionResponse> responses = transactionService
                    .execute("BNK-20250001234567");

            // then
            assertThat(responses)
                    .isNotNull()
                    .isEmpty();
        }

        @Test
        @DisplayName("Debe lanzar excepción cuando la cuenta no existe")
        void shouldThrowWhenAccountNotFound() {
            // given
            when(accountRepositoryPort.findByAccountNumber(anyString()))
                    .thenReturn(Optional.empty());

            // when - then
            assertThatThrownBy(() -> transactionService.execute("BNK-00000000000000"))
                    .isInstanceOf(AccountNotFoundException.class);

            verify(transactionRepositoryPort, never())
                    .findByAccountIdOrderByCreatedAtDesc(anyLong());
        }
    }

    // GET TRANSACTION BY REFERENCE NUMBER
    // =========================================================
    @Nested
    @DisplayName("Obtener transacción por número de referencia")
    class GetTransactionByReferenceNumber {

        @Test
        @DisplayName("Debe retornar la transacción cuando el número de referencia existe")
        void shouldReturnTransactionWhenReferenceNumberExists() {
            // given
            when(transactionRepositoryPort.findByReferenceNumber("TXN-20250001234567"))
                    .thenReturn(Optional.of(completedTransaction));
            when(accountRepositoryPort.findById(1L))
                    .thenReturn(Optional.of(activeAccount));
            when(transactionApplicationMapper.toResponse(any(Transaction.class), any(Account.class)))
                    .thenReturn(expectedResponse);

            // when
            TransactionResponse response = transactionService
                    .executeGetTransaction("TXN-20250001234567");

            // then
            assertThat(response).isNotNull();
            assertThat(response.referenceNumber()).isEqualTo("TXN-20250001234567");
            assertThat(response.accountNumber()).isEqualTo("BNK-20250001234567");
            assertThat(response.amount()).isEqualByComparingTo(new BigDecimal("200.00"));
        }

        @Test
        @DisplayName("Debe lanzar excepción cuando el número de referencia no existe")
        void shouldThrowWhenReferenceNumberNotFound() {
            // given
            when(transactionRepositoryPort.findByReferenceNumber(anyString()))
                    .thenReturn(Optional.empty());

            // when - then
            assertThatThrownBy(() -> transactionService
                    .executeGetTransaction("TXN-00000000000000"))
                    .isInstanceOf(TransactionNotFoundException.class);

            verify(accountRepositoryPort, never()).findById(anyLong());
        }
    }
}
