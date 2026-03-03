package dev.magadiflo.banking.app.account.application.service;

import dev.magadiflo.banking.app.account.application.dto.command.DepositCommand;
import dev.magadiflo.banking.app.account.application.dto.command.OpenAccountCommand;
import dev.magadiflo.banking.app.account.application.dto.command.WithdrawCommand;
import dev.magadiflo.banking.app.account.application.dto.response.AccountBalanceResponse;
import dev.magadiflo.banking.app.account.application.dto.response.AccountResponse;
import dev.magadiflo.banking.app.account.application.helper.AccountApplicationHelper;
import dev.magadiflo.banking.app.account.application.mapper.AccountApplicationMapper;
import dev.magadiflo.banking.app.account.application.port.input.BlockAccountUseCase;
import dev.magadiflo.banking.app.account.application.port.input.DepositUseCase;
import dev.magadiflo.banking.app.account.application.port.input.GetAccountBalanceUseCase;
import dev.magadiflo.banking.app.account.application.port.input.GetAccountByIdUseCase;
import dev.magadiflo.banking.app.account.application.port.input.GetAccountsByCustomerUseCase;
import dev.magadiflo.banking.app.account.application.port.input.OpenAccountUserCase;
import dev.magadiflo.banking.app.account.application.port.input.WithdrawUseCase;
import dev.magadiflo.banking.app.account.application.port.output.AccountRepositoryPort;
import dev.magadiflo.banking.app.account.application.port.output.ExchangeRatePort;
import dev.magadiflo.banking.app.account.domain.exception.AccountNotFoundException;
import dev.magadiflo.banking.app.account.domain.exception.MaxAccountsReachedException;
import dev.magadiflo.banking.app.account.domain.model.Account;
import dev.magadiflo.banking.app.account.domain.model.enums.Currency;
import dev.magadiflo.banking.app.account.domain.model.vo.Money;
import dev.magadiflo.banking.app.customer.application.port.output.CustomerRepositoryPort;
import dev.magadiflo.banking.app.customer.domain.exception.CustomerNotFoundException;
import dev.magadiflo.banking.app.customer.domain.model.Customer;
import dev.magadiflo.banking.app.transaction.application.port.output.TransactionRepositoryPort;
import dev.magadiflo.banking.app.transaction.domain.model.Transaction;
import dev.magadiflo.banking.app.transaction.domain.model.enums.TransactionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class AccountService implements GetAccountsByCustomerUseCase, GetAccountByIdUseCase, GetAccountBalanceUseCase,
        OpenAccountUserCase, DepositUseCase, WithdrawUseCase, BlockAccountUseCase {

    private static final int MAX_ACTIVE_ACCOUNTS = 3;

    private final CustomerRepositoryPort customerRepositoryPort;
    private final AccountRepositoryPort accountRepositoryPort;
    private final TransactionRepositoryPort transactionRepositoryPort;
    private final ExchangeRatePort exchangeRatePort;
    private final AccountApplicationMapper accountApplicationMapper;
    private final AccountApplicationHelper accountApplicationHelper;

    // GET ACCOUNTS BY CUSTOMER
    // =========================================================
    @Override
    public List<AccountResponse> executeAccountsByCustomer(String customerCode) {
        Customer customer = this.findCustomerByCodeOrThrow(customerCode);
        return this.accountRepositoryPort.findByCustomerId(customer.getId().value()).stream()
                .map(account -> this.accountApplicationMapper.toResponse(account, customer))
                .toList();
    }

    // GET BY ACCOUNT NUMBER
    // =========================================================
    @Override
    public AccountResponse execute(String accountNumber) {
        Account account = this.findByAccountNumberOrThrow(accountNumber);
        Customer customer = this.findCustomerByIdOrThrow(account.getCustomerId());
        return this.accountApplicationMapper.toResponse(account, customer);
    }

    // GET BALANCE (con tipo de cambio a USD via API externa)
    // =========================================================
    @Override
    public AccountBalanceResponse executeGetBalance(String accountNumber) {
        // 1. Buscar cuenta
        Account account = this.findByAccountNumberOrThrow(accountNumber);

        // 2. Si la moneda ya es USD no necesitamos llamar a la API
        BigDecimal exchangeRate;
        BigDecimal balanceInUsd;

        if (account.getCurrency() == Currency.USD) {
            exchangeRate = BigDecimal.ONE;
            balanceInUsd = account.getBalance().amount();
        } else {
            // 3. Consultar tasa de cambio via puerto de salida → RestClient en infra
            exchangeRate = this.exchangeRatePort
                    .getExchangeRate(account.getCurrency().name(), Currency.USD.name());
            balanceInUsd = account.getBalance().amount()
                    .multiply(exchangeRate)
                    .setScale(2, RoundingMode.HALF_UP);
        }

        return this.accountApplicationMapper.toBalanceResponse(account, balanceInUsd, exchangeRate);
    }

    // OPEN ACCOUNT
    // =========================================================
    @Override
    @Transactional
    public AccountResponse execute(OpenAccountCommand command) {
        // 1. Buscar y validar que el cliente existe y puede operar
        Customer customer = this.findCustomerByCodeOrThrow(command.customerCode());
        customer.validateIsOperational();

        // 2. Validar que no supere el máximo de cuentas activas
        int activeAccounts = this.accountRepositoryPort.countActiveAccountsByCustomerId(customer.getId().value());
        if (activeAccounts >= MAX_ACTIVE_ACCOUNTS) {
            throw new MaxAccountsReachedException(command.customerCode(), MAX_ACTIVE_ACCOUNTS);
        }

        // 3. Generar número de cuenta
        String accountNumber = this.accountApplicationHelper.generateAccountNumber();

        // 4. Crear la entidad de dominio usando el factory method
        // La validación del saldo inicial vive en Account.open()
        Account account = Account.open(
                accountNumber,
                customer.getId().value(),
                command.accountType(),
                Money.of(command.initialBalance(), command.currency())
        );

        // 5. Persistir y retornar
        Account savedAccount = this.accountRepositoryPort.save(account);
        return this.accountApplicationMapper.toResponse(savedAccount, customer);
    }

    // DEPOSIT
    // =========================================================
    @Override
    @Transactional
    public AccountResponse execute(String accountNumber, DepositCommand command) {
        // 1. Buscar cuenta y cliente
        Account account = this.findByAccountNumberOrThrow(accountNumber);
        Customer customer = this.findCustomerByIdOrThrow(account.getCustomerId());

        // 2. Ejecutar depósito — las reglas de negocio viven en el dominio
        Money amount = Money.of(command.amount(), account.getCurrency());
        account.deposit(amount);

        // 3. Persistir cuenta actualizada
        Account updatedAccount = this.accountRepositoryPort.save(account);

        // 4. Registrar transacción exitosa
        Transaction transaction = Transaction.registerCompleted(
                this.accountApplicationHelper.generateReferenceNumber(),
                account.getId().value(),
                TransactionType.DEPOSIT,
                amount,
                command.description()
        );
        this.transactionRepositoryPort.save(transaction);

        return this.accountApplicationMapper.toResponse(updatedAccount, customer);
    }

    // WITHDRAW
    // =========================================================
    @Override
    @Transactional
    public AccountResponse execute(String accountNumber, WithdrawCommand command) {
        // 1. Buscar cuenta y cliente
        Account account = this.findByAccountNumberOrThrow(accountNumber);
        Customer customer = this.findCustomerByIdOrThrow(account.getCustomerId());

        // 2. Ejecutar retiro — las reglas de negocio viven en el dominio
        Money amount = Money.of(command.amount(), account.getCurrency());
        account.withdraw(amount);

        // 3. Persistir cuenta actualizada
        Account updatedAccount = this.accountRepositoryPort.save(account);

        // 4. Registrar transacción exitosa
        Transaction transaction = Transaction.registerCompleted(
                this.accountApplicationHelper.generateReferenceNumber(),
                account.getId().value(),
                TransactionType.WITHDRAWAL,
                amount,
                command.description()
        );
        this.transactionRepositoryPort.save(transaction);

        return this.accountApplicationMapper.toResponse(updatedAccount, customer);
    }

    // BLOCK ACCOUNT
    // =========================================================
    @Override
    @Transactional
    public void executeBlock(String accountNumber) {
        Account account = this.findByAccountNumberOrThrow(accountNumber);
        account.block();

        this.accountRepositoryPort.save(account);
    }

    // MÉTODOS PRIVADOS DE APOYO
    // =========================================================
    private Customer findCustomerByCodeOrThrow(String customerCode) {
        return this.customerRepositoryPort.findByCustomerCode(customerCode)
                .orElseThrow(() -> new CustomerNotFoundException(customerCode));
    }

    private Customer findCustomerByIdOrThrow(Long customerId) {
        return this.customerRepositoryPort.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException("ID: " + customerId));
    }

    private Account findByAccountNumberOrThrow(String accountNumber) {
        return this.accountRepositoryPort.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException(accountNumber));
    }
}
