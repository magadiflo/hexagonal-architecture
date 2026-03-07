package dev.magadiflo.banking.app.customer.unit;

import dev.magadiflo.banking.app.customer.application.dto.response.CustomerResponse;
import dev.magadiflo.banking.app.customer.application.port.input.CreateCustomerUseCase;
import dev.magadiflo.banking.app.customer.application.port.input.DeleteCustomerUseCase;
import dev.magadiflo.banking.app.customer.application.port.input.GetAllCustomersUseCase;
import dev.magadiflo.banking.app.customer.application.port.input.GetCustomerByIdUseCase;
import dev.magadiflo.banking.app.customer.application.port.input.UpdateCustomerUseCase;
import dev.magadiflo.banking.app.customer.domain.exception.CustomerNotFoundException;
import dev.magadiflo.banking.app.customer.domain.model.enums.CustomerStatus;
import dev.magadiflo.banking.app.customer.domain.model.enums.DocumentType;
import dev.magadiflo.banking.app.customer.infrastructure.adapter.input.rest.CustomerController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CustomerController.class)
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    // Spring Boot 4 usa @MockitoBean en lugar de @MockBean
    @MockitoBean
    private CreateCustomerUseCase createCustomerUseCase;
    @MockitoBean
    private GetCustomerByIdUseCase getCustomerByIdUseCase;
    @MockitoBean
    private GetAllCustomersUseCase getAllCustomersUseCase;
    @MockitoBean
    private UpdateCustomerUseCase updateCustomerUseCase;
    @MockitoBean
    private DeleteCustomerUseCase deleteCustomerUseCase;

    private CustomerResponse buildCustomerResponse() {
        return new CustomerResponse(
                "CUS-2025-123456",
                "12345678",
                DocumentType.DNI,
                "Juan",
                "Pérez",
                "Juan Pérez",
                "juan.perez@gmail.com",
                "987654321",
                CustomerStatus.ACTIVE,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    // POST /api/v1/customers
    // =========================================================
    @Nested
    @DisplayName("POST /api/v1/customers")
    class CreateCustomer {

        @Test
        @DisplayName("Debe retornar 201 cuando el cliente se crea exitosamente")
        void shouldReturn201WhenCustomerCreatedSuccessfully() throws Exception {
            // given
            String requestBody = """
                    {
                        "documentNumber": "12345678",
                        "documentType": "DNI",
                        "firstName": "Juan",
                        "lastName": "Pérez",
                        "email": "juan.perez@gmail.com",
                        "phone": "987654321"
                    }
                    """;

            when(createCustomerUseCase.execute(any())).thenReturn(buildCustomerResponse());

            // when & then
            mockMvc.perform(post("/api/v1/customers")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.customerCode").value("CUS-2025-123456"))
                    .andExpect(jsonPath("$.firstName").value("Juan"))
                    .andExpect(jsonPath("$.status").value("ACTIVE"));
        }

        @Test
        @DisplayName("Debe retornar 400 cuando los campos son inválidos")
        void shouldReturn400WhenFieldsAreInvalid() throws Exception {
            // given
            String requestBody = """
                    {
                        "documentNumber": "",
                        "documentType": "DNI",
                        "firstName": "",
                        "lastName": "Pérez",
                        "email": "invalido",
                        "phone": "987654321"
                    }
                    """;

            // when & then
            mockMvc.perform(post("/api/v1/customers")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestBody))
                    .andExpect(status().isBadRequest());
        }
    }

    // GET /api/v1/customers/{customerCode}
    // =========================================================
    @Nested
    @DisplayName("GET /api/v1/customers/{customerCode}")
    class GetCustomerByCode {

        @Test
        @DisplayName("Debe retornar 200 cuando el cliente existe")
        void shouldReturn200WhenCustomerExists() throws Exception {
            // given
            when(getCustomerByIdUseCase.execute("CUS-2025-123456"))
                    .thenReturn(buildCustomerResponse());

            // when & then
            mockMvc.perform(get("/api/v1/customers/CUS-2025-123456"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.customerCode").value("CUS-2025-123456"))
                    .andExpect(jsonPath("$.fullName").value("Juan Pérez"));
        }

        @Test
        @DisplayName("Debe retornar 404 cuando el cliente no existe")
        void shouldReturn404WhenCustomerNotFound() throws Exception {
            // given
            when(getCustomerByIdUseCase.execute(anyString()))
                    .thenThrow(new CustomerNotFoundException("CUS-2025-000000"));

            // when & then
            mockMvc.perform(get("/api/v1/customers/CUS-2025-000000"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.status").value(404))
                    .andExpect(jsonPath("$.message").exists());
        }
    }

    // DELETE /api/v1/customers/{customerCode}
    // =========================================================
    @Nested
    @DisplayName("DELETE /api/v1/customers/{customerCode}")
    class DeleteCustomer {

        @Test
        @DisplayName("Debe retornar 204 cuando el cliente se elimina exitosamente")
        void shouldReturn204WhenCustomerDeletedSuccessfully() throws Exception {
            // when & then
            mockMvc.perform(delete("/api/v1/customers/CUS-2025-123456"))
                    .andExpect(status().isNoContent());
        }
    }
}
