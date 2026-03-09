package dev.magadiflo.banking.app.account.unit;

import dev.magadiflo.banking.app.account.domain.exception.AccountBlockedException;
import dev.magadiflo.banking.app.account.domain.exception.AccountClosedException;
import dev.magadiflo.banking.app.account.domain.exception.InsufficientFundsException;
import dev.magadiflo.banking.app.account.domain.exception.InvalidAmountException;
import dev.magadiflo.banking.app.account.domain.model.Account;
import dev.magadiflo.banking.app.account.domain.model.enums.AccountStatus;
import dev.magadiflo.banking.app.account.domain.model.enums.AccountType;
import dev.magadiflo.banking.app.account.domain.model.enums.Currency;
import dev.magadiflo.banking.app.account.domain.model.vo.Money;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("Account — Tests Unitarios de Dominio")
class AccountTest {

    // DATOS DE APOYO
    // =========================================================
    private Account buildActiveAccount() {
        return Account.reconstitute(
                1L,
                "BNK-20250001234567",
                1L,
                AccountType.SAVINGS,
                Money.of(new BigDecimal("500.00"), Currency.PEN),
                AccountStatus.ACTIVE,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    private Account buildBlockedAccount() {
        return Account.reconstitute(
                2L,
                "BNK-20250007654321",
                1L,
                AccountType.SAVINGS,
                Money.of(new BigDecimal("500.00"), Currency.PEN),
                AccountStatus.BLOCKED,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    private Account buildClosedAccount() {
        return Account.reconstitute(
                3L,
                "BNK-20250001111111",
                1L,
                AccountType.SAVINGS,
                Money.of(new BigDecimal("500.00"), Currency.PEN),
                AccountStatus.CLOSED,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    // FACTORY METHOD: open()
    // =========================================================
    @Nested
    @DisplayName("Factory method open()")
    class Open {

        @Test
        @DisplayName("Debe inicializar el Account con status ACTIVE, sin id ni fechas asignadas")
        void shouldOpenAccountWithActiveStatus() {
            // given - when
            Account account = Account.open(
                    "BNK-20250001234567",
                    1L,
                    AccountType.SAVINGS,
                    Money.of(new BigDecimal("500.00"), Currency.PEN)
            );

            // then
            assertThat(account).isNotNull();
            assertThat(account)
                    .extracting(Account::getId, Account::getStatus, Account::getCreatedAt, Account::getUpdatedAt)
                    .containsExactly(null, AccountStatus.ACTIVE, null, null);
        }

        @Test
        @DisplayName("Debe inicializar el Account con el saldo inicial correcto")
        void shouldOpenAccountWithCorrectInitialBalance() {
            // given - when
            Account account = Account.open(
                    "BNK-20250001234567",
                    1L,
                    AccountType.SAVINGS,
                    Money.of(new BigDecimal("1000.00"), Currency.PEN)
            );

            // then
            assertThat(account.getBalance().amount())
                    .isEqualByComparingTo("1000");
            assertThat(account.getBalance().currency()).isEqualTo(Currency.PEN);
        }

        @Test
        @DisplayName("Debe lanzar excepción cuando el saldo inicial es cero")
        void shouldThrowWhenInitialBalanceIsZero() {
            // given - when - then
            Money money = Money.of(BigDecimal.ZERO, Currency.PEN);
            assertThatThrownBy(() -> Account.open(
                    "BNK-20250001234567",
                    1L,
                    AccountType.SAVINGS,
                    money
            ))
                    .isExactlyInstanceOf(InvalidAmountException.class)
                    .hasMessage("El monto de la operación debe ser mayor a cero");
        }

        @Test
        @DisplayName("Debe lanzar excepción cuando el saldo inicial es negativo")
        void shouldThrowWhenInitialBalanceIsNegative() {
            // given - when - then
            Money money = Money.of(new BigDecimal("-100.00"), Currency.PEN);
            assertThatThrownBy(() -> Account.open(
                    "BNK-20250001234567",
                    1L,
                    AccountType.SAVINGS,
                    money
            ))
                    .isExactlyInstanceOf(InvalidAmountException.class)
                    .hasMessage("El monto de la operación debe ser mayor a cero");
        }
    }

    // validateIsOperational()
    // =========================================================
    @Nested
    @DisplayName("validateIsOperational()")
    class ValidateIsOperational {

        @Test
        @DisplayName("No debe lanzar excepción cuando la cuenta está ACTIVE")
        void shouldNotThrowWhenAccountIsActive() {
            // given
            Account account = buildActiveAccount();

            // when - then
            assertThatNoException()
                    .isThrownBy(account::validateIsOperational);
        }

        @Test
        @DisplayName("Debe lanzar AccountBlockedException cuando la cuenta está BLOCKED")
        void shouldThrowWhenAccountIsBlocked() {
            // given
            Account account = buildBlockedAccount();

            // when - then
            assertThatThrownBy(account::validateIsOperational)
                    .isExactlyInstanceOf(AccountBlockedException.class)
                    .hasMessage("La cuenta %s se encuentra bloqueada y no puede realizar operaciones"
                            .formatted(account.getAccountNumber().value()));
        }

        @Test
        @DisplayName("Debe lanzar AccountClosedException cuando la cuenta está CLOSED")
        void shouldThrowWhenAccountIsClosed() {
            // given
            Account account = buildClosedAccount();

            // when - then
            assertThatThrownBy(account::validateIsOperational)
                    .isExactlyInstanceOf(AccountClosedException.class)
                    .hasMessageContaining("se encuentra cerrada y no puede realizar operaciones");
        }
    }

    // deposit()
    // =========================================================
    @Nested
    @DisplayName("deposit()")
    class Deposit {

        @Test
        @DisplayName("Debe incrementar el saldo correctamente")
        void shouldIncreaseBalanceCorrectly() {
            // given
            Account account = buildActiveAccount();
            Money amount = Money.of(new BigDecimal("200.00"), Currency.PEN);

            // when
            account.deposit(amount);

            // then
            assertThat(account.getBalance().amount())
                    .isEqualByComparingTo("700");
        }

        @Test
        @DisplayName("Debe lanzar excepción cuando el monto es cero")
        void shouldThrowWhenAmountIsZero() {
            // given
            Account account = buildActiveAccount();
            Money amount = Money.of(BigDecimal.ZERO, Currency.PEN);

            // when - then
            assertThatThrownBy(() -> account.deposit(amount))
                    .isExactlyInstanceOf(InvalidAmountException.class)
                    .hasMessage("El monto de la operación debe ser mayor a cero");
        }

        @Test
        @DisplayName("Debe lanzar excepción cuando se deposita en cuenta bloqueada")
        void shouldThrowWhenDepositingInBlockedAccount() {
            // given
            Account account = buildBlockedAccount();
            Money amount = Money.of(new BigDecimal("200.00"), Currency.PEN);

            // when - then
            assertThatThrownBy(() -> account.deposit(amount))
                    .isExactlyInstanceOf(AccountBlockedException.class)
                    .hasMessageContaining("se encuentra bloqueada y no puede realizar operaciones");
        }
    }

    // withdraw()
    // =========================================================
    @Nested
    @DisplayName("withdraw()")
    class Withdraw {

        @Test
        @DisplayName("Debe decrementar el saldo correctamente")
        void shouldDecreaseBalanceCorrectly() {
            // given
            Account account = buildActiveAccount();
            Money amount = Money.of(new BigDecimal("200.00"), Currency.PEN);

            // when
            account.withdraw(amount);

            // then
            assertThat(account.getBalance().amount())
                    .isEqualByComparingTo("300");
        }

        @Test
        @DisplayName("Debe lanzar excepción cuando el saldo es insuficiente")
        void shouldThrowWhenInsufficientFunds() {
            // given
            Account account = buildActiveAccount();
            Money amount = Money.of(new BigDecimal("9999.00"), Currency.PEN);

            // when - then
            assertThatThrownBy(() -> account.withdraw(amount))
                    .isExactlyInstanceOf(InsufficientFundsException.class)
                    .hasMessageContaining("Salgo insuficiente");
        }

        @Test
        @DisplayName("Debe lanzar excepción cuando el monto es negativo")
        void shouldThrowWhenAmountIsNegative() {
            // given
            Account account = buildActiveAccount();
            Money amount = Money.of(new BigDecimal("-100.00"), Currency.PEN);

            // when - then
            assertThatThrownBy(() -> account.withdraw(amount))
                    .isExactlyInstanceOf(InvalidAmountException.class)
                    .hasMessage("El monto de la operación debe ser mayor a cero");
        }

        @Test
        @DisplayName("Debe lanzar excepción cuando se retira de cuenta bloqueada")
        void shouldThrowWhenWithdrawingFromBlockedAccount() {
            // given
            Account account = buildBlockedAccount();
            Money amount = Money.of(new BigDecimal("100.00"), Currency.PEN);

            // when - then
            assertThatThrownBy(() -> account.withdraw(amount))
                    .isExactlyInstanceOf(AccountBlockedException.class);
        }
    }

    // block()
    // =========================================================
    @Nested
    @DisplayName("block()")
    class Block {

        @Test
        @DisplayName("Debe cambiar el status a BLOCKED cuando la cuenta está ACTIVE")
        void shouldBlockActiveAccount() {
            // given
            Account account = buildActiveAccount();

            // when
            account.block();

            // then
            assertThat(account.getStatus()).isEqualTo(AccountStatus.BLOCKED);
        }

        @Test
        @DisplayName("Debe lanzar excepción cuando la cuenta ya está BLOCKED")
        void shouldThrowWhenAccountAlreadyBlocked() {
            // given
            Account account = buildBlockedAccount();

            // when - then
            assertThatThrownBy(account::block)
                    .isExactlyInstanceOf(AccountBlockedException.class);
        }

        @Test
        @DisplayName("Debe lanzar excepción cuando la cuenta está CLOSED")
        void shouldThrowWhenAccountIsClosed() {
            // given
            Account account = buildClosedAccount();

            // when - then
            assertThatThrownBy(account::block)
                    .isExactlyInstanceOf(AccountClosedException.class);
        }
    }
}
