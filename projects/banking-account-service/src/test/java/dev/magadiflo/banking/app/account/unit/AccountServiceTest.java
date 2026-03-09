package dev.magadiflo.banking.app.account.unit;

import dev.magadiflo.banking.app.account.application.dto.command.DepositCommand;
import dev.magadiflo.banking.app.account.application.dto.command.OpenAccountCommand;
import dev.magadiflo.banking.app.account.application.dto.command.WithdrawCommand;
import dev.magadiflo.banking.app.account.application.dto.response.AccountBalanceResponse;
import dev.magadiflo.banking.app.account.application.dto.response.AccountResponse;
import dev.magadiflo.banking.app.account.application.helper.AccountApplicationHelper;
import dev.magadiflo.banking.app.account.application.mapper.AccountApplicationMapper;
import dev.magadiflo.banking.app.account.application.port.output.AccountRepositoryPort;
import dev.magadiflo.banking.app.account.application.port.output.ExchangeRatePort;
import dev.magadiflo.banking.app.account.application.service.AccountService;
import dev.magadiflo.banking.app.account.domain.exception.AccountNotFoundException;
import dev.magadiflo.banking.app.account.domain.exception.InsufficientFundsException;
import dev.magadiflo.banking.app.account.domain.exception.MaxAccountsReachedException;
import dev.magadiflo.banking.app.account.domain.model.Account;
import dev.magadiflo.banking.app.account.domain.model.enums.AccountStatus;
import dev.magadiflo.banking.app.account.domain.model.enums.AccountType;
import dev.magadiflo.banking.app.account.domain.model.enums.Currency;
import dev.magadiflo.banking.app.account.domain.model.vo.Money;
import dev.magadiflo.banking.app.customer.application.port.output.CustomerRepositoryPort;
import dev.magadiflo.banking.app.customer.domain.exception.CustomerNotFoundException;
import dev.magadiflo.banking.app.customer.domain.model.Customer;
import dev.magadiflo.banking.app.customer.domain.model.enums.CustomerStatus;
import dev.magadiflo.banking.app.customer.domain.model.enums.DocumentType;
import dev.magadiflo.banking.app.transaction.application.port.output.TransactionRepositoryPort;
import dev.magadiflo.banking.app.transaction.domain.model.Transaction;
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
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DisplayName("AccountService — Tests Unitarios")
@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private CustomerRepositoryPort customerRepositoryPort;
    @Mock
    private AccountRepositoryPort accountRepositoryPort;
    @Mock
    private TransactionRepositoryPort transactionRepositoryPort;
    @Mock
    private ExchangeRatePort exchangeRatePort;
    @Mock
    private AccountApplicationMapper accountApplicationMapper;
    @Mock
    private AccountApplicationHelper accountApplicationHelper;

    @InjectMocks
    private AccountService accountService;

    private Customer activeCustomer;
    private Account activeAccount;
    private AccountResponse expectedResponse;

    @BeforeEach
    void setUp() {
        activeCustomer = Customer.reconstitute(
                1L, "CUS-2025-123456", "12345678", DocumentType.DNI,
                "Juan", "Pérez", "juan.perez@gmail.com", "987654321",
                CustomerStatus.ACTIVE, LocalDateTime.now(), LocalDateTime.now()
        );

        activeAccount = Account.reconstitute(
                1L, "BNK-20250001234567", 1L, AccountType.SAVINGS,
                Money.of(new BigDecimal("500.00"), Currency.PEN),
                AccountStatus.ACTIVE, LocalDateTime.now(), LocalDateTime.now()
        );

        expectedResponse = new AccountResponse(
                "BNK-20250001234567", "CUS-2025-123456",
                AccountType.SAVINGS, new BigDecimal("500.00"),
                Currency.PEN, AccountStatus.ACTIVE,
                LocalDateTime.now(), LocalDateTime.now()
        );
    }

    // OPEN ACCOUNT
    // =========================================================
    @Nested
    @DisplayName("Abrir cuenta")
    class OpenAccount {

        @Test
        @DisplayName("Debe abrir una cuenta exitosamente cuando los datos son válidos")
        void shouldOpenAccountSuccessfully() {
            // given
            OpenAccountCommand command = new OpenAccountCommand(
                    "CUS-2025-123456", AccountType.SAVINGS,
                    new BigDecimal("500.00"), Currency.PEN
            );

            when(customerRepositoryPort.findByCustomerCode("CUS-2025-123456"))
                    .thenReturn(Optional.of(activeCustomer));
            when(accountRepositoryPort.countActiveAccountsByCustomerId(1L)).thenReturn(0);
            when(accountApplicationHelper.generateAccountNumber())
                    .thenReturn("BNK-20250001234567");
            when(accountRepositoryPort.save(any(Account.class))).thenReturn(activeAccount);
            when(accountApplicationMapper.toResponse(any(Account.class), any(Customer.class)))
                    .thenReturn(expectedResponse);

            // when
            AccountResponse response = accountService.execute(command);

            // then
            assertThat(response).isNotNull();
            assertThat(response.accountNumber()).isEqualTo("BNK-20250001234567");
            assertThat(response.status()).isEqualTo(AccountStatus.ACTIVE);
            verify(accountRepositoryPort).save(any(Account.class));
        }

        @Test
        @DisplayName("Debe lanzar excepción cuando el cliente no existe")
        void shouldThrowWhenCustomerNotFound() {
            // given
            OpenAccountCommand command = new OpenAccountCommand(
                    "CUS-2025-000000", AccountType.SAVINGS,
                    new BigDecimal("500.00"), Currency.PEN
            );

            when(customerRepositoryPort.findByCustomerCode("CUS-2025-000000"))
                    .thenReturn(Optional.empty());

            // when - then
            assertThatThrownBy(() -> accountService.execute(command))
                    .isExactlyInstanceOf(CustomerNotFoundException.class)
                    .hasMessage("No se encontró el cliente con código: %s".formatted(command.customerCode()));

            verify(accountRepositoryPort, never()).save(any(Account.class));
        }

        @Test
        @DisplayName("Debe lanzar excepción cuando el cliente ya tiene 3 cuentas activas")
        void shouldThrowWhenMaxAccountsReached() {
            // given
            OpenAccountCommand command = new OpenAccountCommand(
                    "CUS-2025-123456", AccountType.SAVINGS,
                    new BigDecimal("500.00"), Currency.PEN
            );

            when(customerRepositoryPort.findByCustomerCode("CUS-2025-123456"))
                    .thenReturn(Optional.of(activeCustomer));
            when(accountRepositoryPort.countActiveAccountsByCustomerId(1L)).thenReturn(3);

            // when - then
            assertThatThrownBy(() -> accountService.execute(command))
                    .isExactlyInstanceOf(MaxAccountsReachedException.class);

            verify(accountRepositoryPort, never()).save(any(Account.class));
        }
    }

    // DEPOSIT
    // =========================================================
    @Nested
    @DisplayName("Depósito")
    class Deposit {

        @Test
        @DisplayName("Debe realizar el depósito y registrar la transacción")
        void shouldDepositAndRegisterTransaction() {
            // given
            DepositCommand command = new DepositCommand(
                    new BigDecimal("200.00"), "Depósito en efectivo"
            );

            when(accountRepositoryPort.findByAccountNumber("BNK-20250001234567"))
                    .thenReturn(Optional.of(activeAccount));
            when(customerRepositoryPort.findById(1L))
                    .thenReturn(Optional.of(activeCustomer));
            when(accountRepositoryPort.save(any(Account.class))).thenReturn(activeAccount);
            when(accountApplicationHelper.generateReferenceNumber())
                    .thenReturn("TXN-20250001234567");
            when(transactionRepositoryPort.save(any(Transaction.class)))
                    .thenReturn(mock(Transaction.class));
            when(accountApplicationMapper.toResponse(any(Account.class), any(Customer.class)))
                    .thenReturn(expectedResponse);

            // when
            AccountResponse response = accountService.execute(
                    "BNK-20250001234567", command
            );

            // then
            assertThat(response).isNotNull();
            verify(accountRepositoryPort).save(any(Account.class));
            verify(transactionRepositoryPort).save(any(Transaction.class));
        }

        @Test
        @DisplayName("Debe lanzar excepción cuando la cuenta no existe")
        void shouldThrowWhenAccountNotFound() {
            // given
            DepositCommand command = new DepositCommand(
                    new BigDecimal("200.00"), "Depósito"
            );

            when(accountRepositoryPort.findByAccountNumber(anyString()))
                    .thenReturn(Optional.empty());

            // when - then
            assertThatThrownBy(() -> accountService.execute("BNK-00000000000000", command))
                    .isExactlyInstanceOf(AccountNotFoundException.class);

            verify(transactionRepositoryPort, never()).save(any(Transaction.class));
        }
    }

    // WITHDRAW
    // =========================================================
    @Nested
    @DisplayName("Retiro")
    class Withdraw {

        @Test
        @DisplayName("Debe realizar el retiro y registrar la transacción")
        void shouldWithdrawAndRegisterTransaction() {
            // given
            WithdrawCommand command = new WithdrawCommand(
                    new BigDecimal("100.00"), "Retiro en cajero"
            );

            when(accountRepositoryPort.findByAccountNumber("BNK-20250001234567"))
                    .thenReturn(Optional.of(activeAccount));
            when(customerRepositoryPort.findById(1L))
                    .thenReturn(Optional.of(activeCustomer));
            when(accountRepositoryPort.save(any(Account.class))).thenReturn(activeAccount);
            when(accountApplicationHelper.generateReferenceNumber())
                    .thenReturn("TXN-20250001234567");
            when(transactionRepositoryPort.save(any(Transaction.class)))
                    .thenReturn(mock(Transaction.class));
            when(accountApplicationMapper.toResponse(any(Account.class), any(Customer.class)))
                    .thenReturn(expectedResponse);

            // when
            AccountResponse response = accountService.execute(
                    "BNK-20250001234567", command
            );

            // then
            assertThat(response).isNotNull();
            verify(accountRepositoryPort).save(any(Account.class));
            verify(transactionRepositoryPort).save(any(Transaction.class));
        }

        @Test
        @DisplayName("Debe lanzar excepción cuando el saldo es insuficiente")
        void shouldThrowWhenInsufficientFunds() {
            // given
            WithdrawCommand command = new WithdrawCommand(
                    new BigDecimal("9999.00"), "Retiro excesivo"
            );

            when(accountRepositoryPort.findByAccountNumber("BNK-20250001234567"))
                    .thenReturn(Optional.of(activeAccount));
            when(customerRepositoryPort.findById(1L))
                    .thenReturn(Optional.of(activeCustomer));

            // when - then
            assertThatThrownBy(() -> accountService.execute(
                    "BNK-20250001234567", command))
                    .isInstanceOf(InsufficientFundsException.class);

            verify(transactionRepositoryPort, never()).save(any(Transaction.class));
        }
    }

    // GET BALANCE
    // =========================================================
    @Nested
    @DisplayName("Consultar saldo")
    class GetBalance {

        @Test
        @DisplayName("Debe retornar el saldo sin llamar a API externa cuando la moneda es USD")
        void shouldReturnBalanceWithoutApiCallWhenCurrencyIsUsd() {
            // given
            Account usdAccount = Account.reconstitute(
                    1L, "BNK-20250001234567", 1L, AccountType.SAVINGS,
                    Money.of(new BigDecimal("100.00"), Currency.USD),
                    AccountStatus.ACTIVE, LocalDateTime.now(), LocalDateTime.now()
            );

            when(accountRepositoryPort.findByAccountNumber("BNK-20250001234567"))
                    .thenReturn(Optional.of(usdAccount));
            when(accountApplicationMapper.toBalanceResponse(
                    any(Account.class), any(BigDecimal.class), any(BigDecimal.class)))
                    .thenReturn(new AccountBalanceResponse(
                            "BNK-20250001234567", new BigDecimal("100.00"),
                            Currency.USD, new BigDecimal("100.00"), BigDecimal.ONE
                    ));

            // when
            AccountBalanceResponse response = accountService.executeGetBalance("BNK-20250001234567");

            // then
            assertThat(response).isNotNull();
            assertThat(response.exchangeRate()).isEqualByComparingTo(BigDecimal.ONE);
            verify(exchangeRatePort, never()).getExchangeRate(anyString(), anyString());
        }

        @Test
        @DisplayName("Debe consultar la API externa cuando la moneda no es USD")
        void shouldCallApiWhenCurrencyIsNotUsd() {
            // given
            when(accountRepositoryPort.findByAccountNumber("BNK-20250001234567"))
                    .thenReturn(Optional.of(activeAccount));
            when(exchangeRatePort.getExchangeRate("PEN", "USD"))
                    .thenReturn(new BigDecimal("0.27"));
            when(accountApplicationMapper.toBalanceResponse(
                    any(Account.class), any(BigDecimal.class), any(BigDecimal.class)))
                    .thenReturn(new AccountBalanceResponse(
                            "BNK-20250001234567", new BigDecimal("500.00"),
                            Currency.PEN, new BigDecimal("135.00"), new BigDecimal("0.27")
                    ));

            // when
            AccountBalanceResponse response = accountService.executeGetBalance("BNK-20250001234567");

            // then
            assertThat(response).isNotNull();
            assertThat(response.exchangeRate()).isEqualByComparingTo(new BigDecimal("0.27"));
            verify(exchangeRatePort).getExchangeRate("PEN", "USD");
        }
    }

    // BLOCK ACCOUNT
    // =========================================================
    @Nested
    @DisplayName("Bloquear cuenta")
    class BlockAccount {

        @Test
        @DisplayName("Debe bloquear la cuenta exitosamente")
        void shouldBlockAccountSuccessfully() {
            // given
            when(accountRepositoryPort.findByAccountNumber("BNK-20250001234567"))
                    .thenReturn(Optional.of(activeAccount));
            when(accountRepositoryPort.save(any(Account.class))).thenReturn(activeAccount);

            // when
            accountService.executeBlock("BNK-20250001234567");

            // then
            verify(accountRepositoryPort).save(any(Account.class));
        }
    }
}
