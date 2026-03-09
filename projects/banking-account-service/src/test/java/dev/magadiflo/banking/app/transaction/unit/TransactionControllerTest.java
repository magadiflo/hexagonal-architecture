package dev.magadiflo.banking.app.transaction.unit;

import dev.magadiflo.banking.app.account.domain.exception.AccountNotFoundException;
import dev.magadiflo.banking.app.account.domain.model.enums.Currency;
import dev.magadiflo.banking.app.transaction.application.dto.response.TransactionResponse;
import dev.magadiflo.banking.app.transaction.application.port.input.GetTransactionByIdUseCase;
import dev.magadiflo.banking.app.transaction.application.port.input.GetTransactionsByAccountUseCase;
import dev.magadiflo.banking.app.transaction.domain.exception.TransactionNotFoundException;
import dev.magadiflo.banking.app.transaction.domain.model.enums.TransactionStatus;
import dev.magadiflo.banking.app.transaction.domain.model.enums.TransactionType;
import dev.magadiflo.banking.app.transaction.infrastructure.adapter.input.rest.TransactionController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("TransactionController — Tests Unitarios")
@WebMvcTest(TransactionController.class)
class TransactionControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GetTransactionsByAccountUseCase getTransactionsByAccountUseCase;

    @MockitoBean
    private GetTransactionByIdUseCase getTransactionByIdUseCase;

    private TransactionResponse buildTransactionResponse() {
        return new TransactionResponse(
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

    // GET /api/v1/transactions/accounts/{accountNumber}
    // =========================================================
    @Nested
    @DisplayName("GET /api/v1/transactions/accounts/{accountNumber}")
    class GetTransactionsByAccount {

        @Test
        @DisplayName("Debe retornar 200 con la lista de transacciones")
        void shouldReturn200WithTransactionList() throws Exception {
            // given
            when(getTransactionsByAccountUseCase.execute("BNK-20250001234567"))
                    .thenReturn(List.of(buildTransactionResponse()));

            // when - then
            mockMvc.perform(get("/api/v1/transactions/accounts/BNK-20250001234567"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].referenceNumber")
                            .value("TXN-20250001234567"))
                    .andExpect(jsonPath("$[0].type").value("DEPOSIT"))
                    .andExpect(jsonPath("$[0].status").value("COMPLETED"));
        }

        @Test
        @DisplayName("Debe retornar 200 con lista vacía cuando no hay transacciones")
        void shouldReturn200WithEmptyList() throws Exception {
            // given
            when(getTransactionsByAccountUseCase.execute("BNK-20250001234567"))
                    .thenReturn(List.of());

            // when - then
            mockMvc.perform(get("/api/v1/transactions/accounts/BNK-20250001234567"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$").isEmpty());
        }

        @Test
        @DisplayName("Debe retornar 404 cuando la cuenta no existe")
        void shouldReturn404WhenAccountNotFound() throws Exception {
            // given
            when(getTransactionsByAccountUseCase.execute(anyString()))
                    .thenThrow(new AccountNotFoundException("BNK-00000000000000"));

            // when - then
            mockMvc.perform(get("/api/v1/transactions/accounts/BNK-00000000000000"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404));
        }
    }

    // GET /api/v1/transactions/{referenceNumber}
    // =========================================================
    @Nested
    @DisplayName("GET /api/v1/transactions/{referenceNumber}")
    class GetTransactionByReferenceNumber {

        @Test
        @DisplayName("Debe retornar 200 con el detalle de la transacción")
        void shouldReturn200WithTransactionDetail() throws Exception {
            // given
            when(getTransactionByIdUseCase.executeGetTransaction("TXN-20250001234567"))
                    .thenReturn(buildTransactionResponse());

            // when - then
            mockMvc.perform(get("/api/v1/transactions/TXN-20250001234567"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.referenceNumber")
                            .value("TXN-20250001234567"))
                    .andExpect(jsonPath("$.amount").value(200.00));
        }

        @Test
        @DisplayName("Debe retornar 404 cuando el número de referencia no existe")
        void shouldReturn404WhenReferenceNumberNotFound() throws Exception {
            // given
            when(getTransactionByIdUseCase.executeGetTransaction(anyString()))
                    .thenThrow(new TransactionNotFoundException("TXN-00000000000000"));

            // when - then
            mockMvc.perform(get("/api/v1/transactions/TXN-00000000000000"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404));
        }
    }
}
