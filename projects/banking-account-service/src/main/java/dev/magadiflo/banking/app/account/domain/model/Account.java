package dev.magadiflo.banking.app.account.domain.model;

import dev.magadiflo.banking.app.account.domain.exception.*;
import dev.magadiflo.banking.app.account.domain.model.enums.AccountStatus;
import dev.magadiflo.banking.app.account.domain.model.enums.AccountType;
import dev.magadiflo.banking.app.account.domain.model.enums.Currency;
import dev.magadiflo.banking.app.account.domain.model.vo.AccountId;
import dev.magadiflo.banking.app.account.domain.model.vo.AccountNumber;
import dev.magadiflo.banking.app.account.domain.model.vo.Money;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Account {

    private AccountId id;
    private AccountNumber accountNumber;
    private Long customerId;             // Referencia al cliente por su ID interno
    private AccountType accountType;
    private Money balance;
    private AccountStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // FACTORY METHODS
    // =========================================================

    /**
     * Abre una nueva cuenta bancaria que aún NO ha sido persistida.
     * El id es null porque aún no ha sido asignado por la BD.
     * El balance inicial debe ser mayor a cero por regla de negocio.
     */
    public static Account open(String accountNumber,
                               Long customerId,
                               AccountType accountType,
                               Money initialBalance) {
        if (initialBalance.isZeroOrNegative()) {
            throw new InvalidAmountException();
        }

        Account account = new Account();
        account.accountNumber = new AccountNumber(accountNumber);
        account.customerId = customerId;
        account.accountType = accountType;
        account.balance = initialBalance;
        account.status = AccountStatus.ACTIVE;

        return account;
    }

    /**
     * Reconstituye una cuenta existente desde la base de datos.
     * Usado por el mapper de infraestructura.
     */
    public static Account reconstitute(Long id,
                                       String accountNumber,
                                       Long customerId,
                                       AccountType accountType,
                                       Money balance,
                                       AccountStatus status,
                                       LocalDateTime createdAt,
                                       LocalDateTime updatedAt) {

        Account account = new Account();
        account.id = new AccountId(id);
        account.accountNumber = new AccountNumber(accountNumber);
        account.customerId = customerId;
        account.accountType = accountType;
        account.balance = balance;
        account.status = status;
        account.createdAt = createdAt;
        account.updatedAt = updatedAt;

        return account;
    }

    // COMPORTAMIENTO / REGLAS DE NEGOCIO
    // =========================================================

    /**
     * Verifica que la cuenta puede operar.
     * Lanza excepción según el estado actual de la cuenta.
     */
    public void validateIsOperational() {
        switch (this.status) {
            case BLOCKED -> throw new AccountBlockedException(this.accountNumber.value());
            case CLOSED -> throw new AccountClosedException(this.accountNumber.value());
            case INACTIVE -> throw new AccountInactiveException(this.accountNumber.value());
            default -> { /* ACTIVE: puede operar */ }
        }
    }

    /**
     * Realiza un depósito en la cuenta.
     * Valida que la cuenta esté operativa y que el monto sea positivo.
     */
    public void deposit(Money amount) {
        this.validateIsOperational();
        if (amount.isZeroOrNegative()) {
            throw new InvalidAmountException();
        }
        this.balance = this.balance.add(amount);
    }

    /**
     * Realiza un retiro de la cuenta.
     * Valida operatividad, monto positivo y saldo suficiente.
     */
    public void withdraw(Money amount) {
        this.validateIsOperational();
        if (amount.isZeroOrNegative()) {
            throw new InvalidAmountException();
        }
        if (amount.isGreaterThan(this.balance)) {
            throw new InsufficientFundsException(
                    this.balance.amount(),
                    amount.amount()
            );
        }
        this.balance = this.balance.subtract(amount);
    }

    /**
     * Bloquea la cuenta.
     * Una cuenta CLOSED no puede ser bloqueada.
     */
    public void block() {
        if (this.status == AccountStatus.CLOSED) {
            throw new AccountClosedException(this.accountNumber.value());
        }
        this.status = AccountStatus.BLOCKED;
    }

    /**
     * Retorna la moneda de la cuenta.
     * Method de conveniencia para evitar account.getBalance().currency()
     */
    public Currency getCurrency() {
        return this.balance.currency();
    }
}
