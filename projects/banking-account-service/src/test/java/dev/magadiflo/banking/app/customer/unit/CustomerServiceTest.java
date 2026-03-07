package dev.magadiflo.banking.app.customer.unit;

import dev.magadiflo.banking.app.customer.application.dto.command.CreateCustomerCommand;
import dev.magadiflo.banking.app.customer.application.dto.command.UpdateCustomerCommand;
import dev.magadiflo.banking.app.customer.application.dto.response.CustomerResponse;
import dev.magadiflo.banking.app.customer.application.helper.CustomerApplicationHelper;
import dev.magadiflo.banking.app.customer.application.mapper.CustomerApplicationMapper;
import dev.magadiflo.banking.app.customer.application.port.output.CustomerRepositoryPort;
import dev.magadiflo.banking.app.customer.application.service.CustomerService;
import dev.magadiflo.banking.app.customer.domain.exception.CustomerAlreadyExistsException;
import dev.magadiflo.banking.app.customer.domain.exception.CustomerNotFoundException;
import dev.magadiflo.banking.app.customer.domain.model.Customer;
import dev.magadiflo.banking.app.customer.domain.model.enums.CustomerStatus;
import dev.magadiflo.banking.app.customer.domain.model.enums.DocumentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepositoryPort customerRepositoryPort;
    @Mock
    private CustomerApplicationMapper customerApplicationMapper;
    @Mock
    private CustomerApplicationHelper customerApplicationHelper;
    @InjectMocks
    private CustomerService customerService;

    // =========================================================
    // DATOS DE PRUEBA REUTILIZABLES
    // =========================================================
    private CreateCustomerCommand validCommand;
    private Customer savedCustomer;
    private CustomerResponse expectedResponse;

    @BeforeEach
    void setUp() {
        this.validCommand = new CreateCustomerCommand(
                "12345678",
                DocumentType.DNI,
                "Juan",
                "Pérez",
                "juan.perez@gmail.com",
                "987654321"
        );

        this.savedCustomer = Customer.reconstitute(
                1L,
                "CUS-2025-123456",
                "12345678",
                DocumentType.DNI,
                "Juan",
                "Pérez",
                "juan.perez@gmail.com",
                "987654321",
                CustomerStatus.ACTIVE,
                LocalDateTime.now(),
                LocalDateTime.now()
        );

        this.expectedResponse = new CustomerResponse(
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

    // CREATE CUSTOMER
    // =========================================================
    @Nested
    @DisplayName("Crear cliente")
    class CreateCustomer {
        @Test
        @DisplayName("Debe crear un cliente exitosamente cuando los datos son válidos")
        void shouldCreateCustomerSuccessfully() {
            // given
            when(customerRepositoryPort.existsByDocumentNumber(anyString())).thenReturn(false);
            when(customerRepositoryPort.existsByEmail(anyString())).thenReturn(false);
            when(customerApplicationHelper.generateCustomerCode()).thenReturn("CUS-2025-123456");
            when(customerRepositoryPort.save(any(Customer.class))).thenReturn(savedCustomer);
            when(customerApplicationMapper.toResponse(any(Customer.class))).thenReturn(expectedResponse);

            // when
            CustomerResponse response = customerService.execute(validCommand);

            // then
            assertThat(response).isNotNull();
            assertThat(response.customerCode()).isEqualTo("CUS-2025-123456");
            assertThat(response.firstName()).isEqualTo("Juan");
            assertThat(response.email()).isEqualTo("juan.perez@gmail.com");
            assertThat(response.status()).isEqualTo(CustomerStatus.ACTIVE);

            // Verificamos que se llamaron los métodos correctos
            verify(customerRepositoryPort).existsByDocumentNumber("12345678");
            verify(customerRepositoryPort).existsByEmail("juan.perez@gmail.com");
            verify(customerApplicationHelper).generateCustomerCode();
            verify(customerRepositoryPort).save(any(Customer.class));
        }

        @Test
        @DisplayName("Debe lanzar excepción cuando el número de documento ya existe")
        void shouldThrowExceptionWhenDocumentNumberAlreadyExists() {
            // given
            when(customerRepositoryPort.existsByDocumentNumber(anyString())).thenReturn(true);

            // when & then
            assertThatThrownBy(() -> customerService.execute(validCommand))
                    .isInstanceOf(CustomerAlreadyExistsException.class)
                    .hasMessageContaining("12345678");

            // Verificamos que no se continuó con el flujo
            verify(customerRepositoryPort, never()).existsByEmail(anyString());
            verify(customerRepositoryPort, never()).save(any(Customer.class));
        }

        @Test
        @DisplayName("Debe lanzar excepción cuando el email ya existe")
        void shouldThrowExceptionWhenEmailAlreadyExists() {
            // given
            when(customerRepositoryPort.existsByDocumentNumber(anyString())).thenReturn(false);
            when(customerRepositoryPort.existsByEmail(anyString())).thenReturn(true);

            // when & then
            assertThatThrownBy(() -> customerService.execute(validCommand))
                    .isInstanceOf(CustomerAlreadyExistsException.class)
                    .hasMessageContaining("juan.perez@gmail.com");

            verify(customerRepositoryPort, never()).save(any(Customer.class));
        }
    }

    // GET CUSTOMER BY CODE
    // =========================================================
    @Nested
    @DisplayName("Obtener cliente por código")
    class GetCustomerByCode {
        @Test
        @DisplayName("Debe retornar el cliente cuando el código existe")
        void shouldReturnCustomerWhenCodeExists() {
            // given
            when(customerRepositoryPort.findByCustomerCode("CUS-2025-123456"))
                    .thenReturn(Optional.of(savedCustomer));
            when(customerApplicationMapper.toResponse(savedCustomer))
                    .thenReturn(expectedResponse);

            // when
            CustomerResponse response = customerService.execute("CUS-2025-123456");

            // then
            assertThat(response).isNotNull();
            assertThat(response.customerCode()).isEqualTo("CUS-2025-123456");
            verify(customerRepositoryPort).findByCustomerCode("CUS-2025-123456");
        }

        @Test
        @DisplayName("Debe lanzar excepción cuando el código no existe")
        void shouldThrowExceptionWhenCodeNotFound() {
            // given
            when(customerRepositoryPort.findByCustomerCode(anyString()))
                    .thenReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> customerService.execute("CUS-2025-000000"))
                    .isInstanceOf(CustomerNotFoundException.class)
                    .hasMessageContaining("CUS-2025-000000");
        }
    }

    // GET ALL CUSTOMERS
    // =========================================================
    @Nested
    @DisplayName("Listar todos los clientes")
    class GetAllCustomers {
        @Test
        @DisplayName("Debe retornar lista con todos los clientes")
        void shouldReturnAllCustomers() {
            // given
            when(customerRepositoryPort.findAll()).thenReturn(List.of(savedCustomer));
            when(customerApplicationMapper.toResponse(savedCustomer)).thenReturn(expectedResponse);

            // when
            List<CustomerResponse> responses = customerService.execute();

            // then
            assertThat(responses)
                    .isNotNull()
                    .hasSize(1)
                    .first()
                    .satisfies(r -> {
                        assertThat(r.customerCode()).isEqualTo("CUS-2025-123456");
                        assertThat(r.status()).isEqualTo(CustomerStatus.ACTIVE);
                    });
        }

        @Test
        @DisplayName("Debe retornar lista vacía cuando no hay clientes")
        void shouldReturnEmptyListWhenNoCustomers() {
            // given
            when(customerRepositoryPort.findAll()).thenReturn(List.of());

            // when
            List<CustomerResponse> responses = customerService.execute();

            // then
            assertThat(responses)
                    .isNotNull()
                    .isEmpty();
        }
    }

    // UPDATE CUSTOMER
    // =========================================================
    @Nested
    @DisplayName("Actualizar cliente")
    class UpdateCustomer {
        @Test
        @DisplayName("Debe actualizar el cliente exitosamente")
        void shouldUpdateCustomerSuccessfully() {
            // given
            var command = new UpdateCustomerCommand("Juan Carlos", "Pérez López", "999888777");

            when(customerRepositoryPort.findByCustomerCode("CUS-2025-123456"))
                    .thenReturn(Optional.of(savedCustomer));
            when(customerRepositoryPort.save(any(Customer.class))).thenReturn(savedCustomer);
            when(customerApplicationMapper.toResponse(any(Customer.class)))
                    .thenReturn(expectedResponse);

            // when
            CustomerResponse response = customerService.execute("CUS-2025-123456", command);

            // then
            assertThat(response).isNotNull();
            verify(customerRepositoryPort).save(any(Customer.class));
        }
    }

    // DELETE CUSTOMER
    // =========================================================
    @Nested
    @DisplayName("Eliminar cliente (baja lógica)")
    class DeleteCustomer {

        @Test
        @DisplayName("Debe desactivar el cliente exitosamente")
        void shouldDeactivateCustomerSuccessfully() {
            // given
            when(customerRepositoryPort.findByCustomerCode("CUS-2025-123456"))
                    .thenReturn(Optional.of(savedCustomer));
            when(customerRepositoryPort.save(any(Customer.class))).thenReturn(savedCustomer);

            // when
            customerService.executeDelete("CUS-2025-123456");

            // then
            verify(customerRepositoryPort).findByCustomerCode("CUS-2025-123456");
            verify(customerRepositoryPort).save(any(Customer.class));
        }
    }
}
