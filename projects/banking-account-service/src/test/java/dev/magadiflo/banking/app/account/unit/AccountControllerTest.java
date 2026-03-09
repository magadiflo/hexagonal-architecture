package dev.magadiflo.banking.app.account.unit;

import dev.magadiflo.banking.app.account.application.dto.response.AccountBalanceResponse;
import dev.magadiflo.banking.app.account.application.dto.response.AccountResponse;
import dev.magadiflo.banking.app.account.application.port.input.BlockAccountUseCase;
import dev.magadiflo.banking.app.account.application.port.input.DepositUseCase;
import dev.magadiflo.banking.app.account.application.port.input.GetAccountBalanceUseCase;
import dev.magadiflo.banking.app.account.application.port.input.GetAccountByIdUseCase;
import dev.magadiflo.banking.app.account.application.port.input.GetAccountsByCustomerUseCase;
import dev.magadiflo.banking.app.account.application.port.input.OpenAccountUserCase;
import dev.magadiflo.banking.app.account.application.port.input.WithdrawUseCase;
import dev.magadiflo.banking.app.account.domain.exception.AccountNotFoundException;
import dev.magadiflo.banking.app.account.domain.exception.InsufficientFundsException;
import dev.magadiflo.banking.app.account.domain.model.enums.AccountStatus;
import dev.magadiflo.banking.app.account.domain.model.enums.AccountType;
import dev.magadiflo.banking.app.account.domain.model.enums.Currency;
import dev.magadiflo.banking.app.account.infrastructure.adapter.input.rest.AccountController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("AccountController — Tests Unitarios")
@WebMvcTest(AccountController.class)
class AccountControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OpenAccountUserCase openAccountUseCase;
    @MockitoBean
    private GetAccountByIdUseCase getAccountByIdUseCase;
    @MockitoBean
    private GetAccountsByCustomerUseCase getAccountsByCustomerUseCase;
    @MockitoBean
    private DepositUseCase depositUseCase;
    @MockitoBean
    private WithdrawUseCase withdrawUseCase;
    @MockitoBean
    private GetAccountBalanceUseCase getAccountBalanceUseCase;
    @MockitoBean
    private BlockAccountUseCase blockAccountUseCase;

    private AccountResponse buildAccountResponse() {
        return new AccountResponse(
                "BNK-20250001234567", "CUS-2025-123456",
                AccountType.SAVINGS, new BigDecimal("500.00"),
                Currency.PEN, AccountStatus.ACTIVE,
                LocalDateTime.now(), LocalDateTime.now()
        );
    }

    // POST /api/v1/accounts
    // =========================================================
    @Nested
    @DisplayName("POST /api/v1/accounts")
    class OpenAccount {

        @Test
        @DisplayName("Debe retornar 201 cuando la cuenta se abre exitosamente")
        void shouldReturn201WhenAccountOpenedSuccessfully() throws Exception {
            // given
            String requestBody = """
                    {
                        "accountType": "SAVINGS",
                        "initialBalance": 500.00,
                        "currency": "PEN"
                    }
                    """;

            when(openAccountUseCase.execute(any())).thenReturn(buildAccountResponse());

            // when - then
            mockMvc.perform(post("/api/v1/accounts")
                            .param("customerCode", "CUS-2025-123456")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.accountNumber").value("BNK-20250001234567"))
                    .andExpect(jsonPath("$.status").value("ACTIVE"));
        }

        @Test
        @DisplayName("Debe retornar 400 cuando el saldo inicial es cero")
        void shouldReturn400WhenInitialBalanceIsZero() throws Exception {
            // given
            String requestBody = """
                    {
                        "accountType": "SAVINGS",
                        "initialBalance": 0.00,
                        "currency": "PEN"
                    }
                    """;

            // when - then
            mockMvc.perform(post("/api/v1/accounts")
                            .param("customerCode", "CUS-2025-123456")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isBadRequest());
        }
    }

    // GET /api/v1/accounts/{accountNumber}
    // =========================================================
    @Nested
    @DisplayName("GET /api/v1/accounts/{accountNumber}")
    class GetAccountByNumber {

        @Test
        @DisplayName("Debe retornar 200 cuando la cuenta existe")
        void shouldReturn200WhenAccountExists() throws Exception {
            // given
            when(getAccountByIdUseCase.execute("BNK-20250001234567"))
                    .thenReturn(buildAccountResponse());

            // when - then
            mockMvc.perform(get("/api/v1/accounts/BNK-20250001234567"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.accountNumber").value("BNK-20250001234567"))
                    .andExpect(jsonPath("$.currency").value("PEN"));
        }

        @Test
        @DisplayName("Debe retornar 404 cuando la cuenta no existe")
        void shouldReturn404WhenAccountNotFound() throws Exception {
            // given
            when(getAccountByIdUseCase.execute(anyString()))
                    .thenThrow(new AccountNotFoundException("BNK-00000000000000"));

            // when - then
            mockMvc.perform(get("/api/v1/accounts/BNK-00000000000000"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404));
        }
    }

    // POST /api/v1/accounts/{accountNumber}/deposit
    // =========================================================
    @Nested
    @DisplayName("POST /api/v1/accounts/{accountNumber}/deposit")
    class Deposit {

        @Test
        @DisplayName("Debe retornar 200 cuando el depósito es exitoso")
        void shouldReturn200WhenDepositSuccessful() throws Exception {
            // given
            String requestBody = """
                    {
                        "amount": 200.00,
                        "description": "Depósito en efectivo"
                    }
                    """;

            when(depositUseCase.execute(anyString(), any())).thenReturn(buildAccountResponse());

            // when - then
            mockMvc.perform(post("/api/v1/accounts/BNK-20250001234567/deposit")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.accountNumber").value("BNK-20250001234567"));
        }

        @Test
        @DisplayName("Debe retornar 400 cuando el monto es cero")
        void shouldReturn400WhenAmountIsZero() throws Exception {
            // given
            String requestBody = """
                    {
                        "amount": 0.00,
                        "description": "Depósito inválido"
                    }
                    """;

            // when - then
            mockMvc.perform(post("/api/v1/accounts/BNK-20250001234567/deposit")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isBadRequest());
        }
    }

    // POST /api/v1/accounts/{accountNumber}/withdraw
    // =========================================================
    @Nested
    @DisplayName("POST /api/v1/accounts/{accountNumber}/withdraw")
    class Withdraw {

        @Test
        @DisplayName("Debe retornar 422 cuando el saldo es insuficiente")
        void shouldReturn422WhenInsufficientFunds() throws Exception {
            // given
            String requestBody = """
                    {
                        "amount": 9999.00,
                        "description": "Retiro excesivo"
                    }
                    """;

            when(withdrawUseCase.execute(anyString(), any()))
                    .thenThrow(new InsufficientFundsException(new BigDecimal("100"), new BigDecimal("9999")));

            // when - then
            mockMvc.perform(post("/api/v1/accounts/BNK-20250001234567/withdraw")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isUnprocessableContent())
                    .andExpect(jsonPath("$.status").value(422));
        }
    }

    // GET /api/v1/accounts/{accountNumber}/balance
    // =========================================================
    @Nested
    @DisplayName("GET /api/v1/accounts/{accountNumber}/balance")
    class GetBalance {

        @Test
        @DisplayName("Debe retornar 200 con el saldo y tipo de cambio")
        void shouldReturn200WithBalanceAndExchangeRate() throws Exception {
            // given
            AccountBalanceResponse balanceResponse = new AccountBalanceResponse(
                    "BNK-20250001234567",
                    new BigDecimal("500.00"),
                    Currency.PEN,
                    new BigDecimal("135.00"),
                    new BigDecimal("0.27")
            );

            when(getAccountBalanceUseCase.executeGetBalance("BNK-20250001234567"))
                    .thenReturn(balanceResponse);

            // when - then
            mockMvc.perform(get("/api/v1/accounts/BNK-20250001234567/balance"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.balance").value(500.00))
                    .andExpect(jsonPath("$.balanceInUsd").value(135.00))
                    .andExpect(jsonPath("$.exchangeRate").value(0.27));
        }
    }

    // PATCH /api/v1/accounts/{accountNumber}/block
    // =========================================================
    @Nested
    @DisplayName("PATCH /api/v1/accounts/{accountNumber}/block")
    class BlockAccount {

        @Test
        @DisplayName("Debe retornar 204 cuando la cuenta se bloquea exitosamente")
        void shouldReturn204WhenAccountBlockedSuccessfully() throws Exception {
            // given
            String accountNumber = "BNK-20250001234567";
            doNothing().when(blockAccountUseCase).executeBlock(accountNumber);

            // when - then
            mockMvc.perform(patch("/api/v1/accounts/%s/block".formatted(accountNumber)))
                    .andExpect(status().isNoContent());
        }
    }
}
