package dev.magadiflo.banking.app.account.infrastructure.adapter.input.rest;

import dev.magadiflo.banking.app.account.application.dto.command.DepositCommand;
import dev.magadiflo.banking.app.account.application.dto.command.OpenAccountCommand;
import dev.magadiflo.banking.app.account.application.dto.command.WithdrawCommand;
import dev.magadiflo.banking.app.account.application.dto.response.AccountBalanceResponse;
import dev.magadiflo.banking.app.account.application.dto.response.AccountResponse;
import dev.magadiflo.banking.app.account.application.port.input.BlockAccountUseCase;
import dev.magadiflo.banking.app.account.application.port.input.DepositUseCase;
import dev.magadiflo.banking.app.account.application.port.input.GetAccountBalanceUseCase;
import dev.magadiflo.banking.app.account.application.port.input.GetAccountByIdUseCase;
import dev.magadiflo.banking.app.account.application.port.input.GetAccountsByCustomerUseCase;
import dev.magadiflo.banking.app.account.application.port.input.OpenAccountUserCase;
import dev.magadiflo.banking.app.account.application.port.input.WithdrawUseCase;
import dev.magadiflo.banking.app.account.infrastructure.adapter.input.rest.dto.DepositRequest;
import dev.magadiflo.banking.app.account.infrastructure.adapter.input.rest.dto.OpenAccountRequest;
import dev.magadiflo.banking.app.account.infrastructure.adapter.input.rest.dto.WithdrawRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/api/{version}/accounts", version = "1")
public class AccountController {

    private final GetAccountsByCustomerUseCase getAccountsByCustomerUseCase;
    private final GetAccountByIdUseCase getAccountByIdUseCase;
    private final GetAccountBalanceUseCase getAccountBalanceUseCase;
    private final OpenAccountUserCase openAccountUserCase;
    private final DepositUseCase depositUseCase;
    private final WithdrawUseCase withdrawUseCase;
    private final BlockAccountUseCase blockAccountUseCase;

    @GetMapping(path = "/customers/{customerCode}")
    public ResponseEntity<List<AccountResponse>> getAllAccounts(@PathVariable String customerCode) {
        return ResponseEntity.ok(this.getAccountsByCustomerUseCase.executeAccountsByCustomer(customerCode));
    }

    @GetMapping(path = "/{accountNumber}")
    public ResponseEntity<AccountResponse> getAccount(@PathVariable String accountNumber) {
        return ResponseEntity.ok(this.getAccountByIdUseCase.execute(accountNumber));
    }

    @GetMapping(path = "/{accountNumber}/balance")
    public ResponseEntity<AccountBalanceResponse> getBalance(@PathVariable String accountNumber) {
        return ResponseEntity.ok(this.getAccountBalanceUseCase.executeGetBalance(accountNumber));
    }

    @PostMapping
    public ResponseEntity<AccountResponse> openAccount(@RequestParam String customerCode,
                                                       @Valid @RequestBody OpenAccountRequest request) {
        var command = new OpenAccountCommand(
                customerCode,
                request.accountType(),
                request.initialBalance(),
                request.currency()
        );
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(this.openAccountUserCase.execute(command));
    }

    @PostMapping(path = "/{accountNumber}/deposit")
    public ResponseEntity<AccountResponse> deposit(@PathVariable String accountNumber,
                                                   @Valid @RequestBody DepositRequest request) {
        var command = new DepositCommand(request.amount(), request.description());
        return ResponseEntity.ok(this.depositUseCase.execute(accountNumber, command));
    }

    @PostMapping(path = "/{accountNumber}/withdraw")
    public ResponseEntity<AccountResponse> withdraw(@PathVariable String accountNumber,
                                                    @Valid @RequestBody WithdrawRequest request) {
        var command = new WithdrawCommand(request.amount(), request.description());
        return ResponseEntity.ok(this.withdrawUseCase.execute(accountNumber, command));
    }

    @PatchMapping(path = "/{accountNumber}/block")
    public ResponseEntity<Void> blockAccount(@PathVariable String accountNumber) {
        this.blockAccountUseCase.executeBlock(accountNumber);
        return ResponseEntity.noContent().build();
    }
}
