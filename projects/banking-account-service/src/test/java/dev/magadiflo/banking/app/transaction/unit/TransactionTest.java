package dev.magadiflo.banking.app.transaction.unit;

import dev.magadiflo.banking.app.account.domain.model.enums.Currency;
import dev.magadiflo.banking.app.account.domain.model.vo.Money;
import dev.magadiflo.banking.app.transaction.domain.model.Transaction;
import dev.magadiflo.banking.app.transaction.domain.model.enums.TransactionStatus;
import dev.magadiflo.banking.app.transaction.domain.model.enums.TransactionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Transaction — Tests Unitarios de Dominio")
class TransactionTest {

    // DATOS DE APOYO
    // =========================================================
    private Money buildMoney() {
        return Money.of(new BigDecimal("200.00"), Currency.PEN);
    }

    // FACTORY METHOD: registerCompleted()
    // =========================================================
    @Nested
    @DisplayName("Factory method registerCompleted()")
    class RegisterCompleted {

        @Test
        @DisplayName("Debe inicializar la transacción con status COMPLETED y sin id ni fecha asignados")
        void shouldCreateCompletedTransactionWithoutIdAndDate() {
            // given - when
            Transaction transaction = Transaction.registerCompleted(
                    "TXN-20250001234567",
                    1L,
                    TransactionType.DEPOSIT,
                    buildMoney(),
                    "Depósito en efectivo"
            );

            // then
            assertThat(transaction.getStatus()).isEqualTo(TransactionStatus.COMPLETED);
            assertThat(transaction.getId()).isNull();
            assertThat(transaction.getCreatedAt()).isNull();
        }

        @Test
        @DisplayName("Debe inicializar la transacción con los datos correctos")
        void shouldCreateTransactionWithCorrectData() {
            // given - when
            Transaction transaction = Transaction.registerCompleted(
                    "TXN-20250001234567",
                    1L,
                    TransactionType.DEPOSIT,
                    buildMoney(),
                    "Depósito en efectivo"
            );

            // then
            assertThat(transaction.getReferenceNumber().value())
                    .isEqualTo("TXN-20250001234567");
            assertThat(transaction.getAccountId()).isEqualTo(1L);
            assertThat(transaction.getType()).isEqualTo(TransactionType.DEPOSIT);
            assertThat(transaction.getAmount().amount())
                    .isEqualByComparingTo(new BigDecimal("200.00"));
            assertThat(transaction.getAmount().currency()).isEqualTo(Currency.PEN);
            assertThat(transaction.getDescription()).isEqualTo("Depósito en efectivo");
        }
    }

    // FACTORY METHOD: registerFailed()
    // =========================================================
    @Nested
    @DisplayName("Factory method registerFailed()")
    class RegisterFailed {

        @Test
        @DisplayName("Debe inicializar la transacción con status FAILED y sin id ni fecha asignados")
        void shouldCreateFailedTransactionWithoutIdAndDate() {
            // given - when
            Transaction transaction = Transaction.registerFailed(
                    "TXN-20250001234567",
                    1L,
                    TransactionType.WITHDRAWAL,
                    buildMoney(),
                    "Retiro fallido"
            );

            // then
            assertThat(transaction.getStatus()).isEqualTo(TransactionStatus.FAILED);
            assertThat(transaction.getId()).isNull();
            assertThat(transaction.getCreatedAt()).isNull();
        }
    }

    // isCompleted() y isFailed()
    // =========================================================
    @Nested
    @DisplayName("isCompleted() y isFailed()")
    class StatusChecks {

        @Test
        @DisplayName("isCompleted() debe retornar true cuando el status es COMPLETED")
        void shouldReturnTrueWhenStatusIsCompleted() {
            // given
            Transaction transaction = Transaction.registerCompleted(
                    "TXN-20250001234567", 1L,
                    TransactionType.DEPOSIT, buildMoney(), "Depósito"
            );

            // when - then
            assertThat(transaction.isCompleted()).isTrue();
            assertThat(transaction.isFailed()).isFalse();
        }

        @Test
        @DisplayName("isFailed() debe retornar true cuando el status es FAILED")
        void shouldReturnTrueWhenStatusIsFailed() {
            // given
            Transaction transaction = Transaction.registerFailed(
                    "TXN-20250001234567", 1L,
                    TransactionType.WITHDRAWAL, buildMoney(), "Retiro fallido"
            );

            // when - then
            assertThat(transaction.isFailed()).isTrue();
            assertThat(transaction.isCompleted()).isFalse();
        }
    }
}
