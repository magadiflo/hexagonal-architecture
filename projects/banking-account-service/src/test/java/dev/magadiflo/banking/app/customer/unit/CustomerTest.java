package dev.magadiflo.banking.app.customer.unit;

import dev.magadiflo.banking.app.customer.domain.exception.CustomerBlockedException;
import dev.magadiflo.banking.app.customer.domain.exception.CustomerInactiveException;
import dev.magadiflo.banking.app.customer.domain.model.Customer;
import dev.magadiflo.banking.app.customer.domain.model.enums.CustomerStatus;
import dev.magadiflo.banking.app.customer.domain.model.enums.DocumentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class CustomerTest {

    // DATOS DE APOYO
    // =========================================================
    private Customer buildActiveCustomer() {
        return Customer.reconstitute(
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
    }

    private Customer buildBlockedCustomer() {
        return Customer.reconstitute(
                2L,
                "CUS-2025-654321",
                "87654321",
                DocumentType.DNI,
                "Ana",
                "García",
                "ana.garcia@gmail.com",
                "912345678",
                CustomerStatus.BLOCKED,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    private Customer buildInactiveCustomer() {
        return Customer.reconstitute(
                3L,
                "CUS-2025-111111",
                "11111111",
                DocumentType.DNI,
                "Luis",
                "Torres",
                "luis.torres@gmail.com",
                "911111111",
                CustomerStatus.INACTIVE,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    // FACTORY METHOD: create()
    // =========================================================
    @Nested
    @DisplayName("Factory method create()")
    class Create {

        @Test
        @DisplayName("Debe crear un cliente con status ACTIVE por defecto")
        void shouldCreateCustomerWithActiveStatus() {
            // given - when
            Customer customer = Customer.create(
                    "CUS-2025-123456",
                    "12345678",
                    DocumentType.DNI,
                    "Juan",
                    "Pérez",
                    "juan.perez@gmail.com",
                    "987654321"
            );

            // then
            assertThat(customer.getStatus()).isEqualTo(CustomerStatus.ACTIVE);
            assertThat(customer.getId()).isNull();
            assertThat(customer.getCreatedAt()).isNull();
            assertThat(customer.getUpdatedAt()).isNull();
        }

        @Test
        @DisplayName("Debe crear un cliente con los datos correctos")
        void shouldCreateCustomerWithCorrectData() {
            // given - when
            Customer customer = Customer.create(
                    "CUS-2025-123456",
                    "12345678",
                    DocumentType.DNI,
                    "Juan",
                    "Pérez",
                    "juan.perez@gmail.com",
                    "987654321"
            );

            // then
            assertThat(customer.getFirstName()).isEqualTo("Juan");
            assertThat(customer.getLastName()).isEqualTo("Pérez");
            assertThat(customer.getDocumentNumber().value()).isEqualTo("12345678");
            assertThat(customer.getEmail().value()).isEqualTo("juan.perez@gmail.com");
            assertThat(customer.getCustomerCode().value()).isEqualTo("CUS-2025-123456");
        }
    }

    // getFullName()
    // =========================================================
    @Nested
    @DisplayName("getFullName()")
    class GetFullName {

        @Test
        @DisplayName("Debe retornar el nombre completo correctamente")
        void shouldReturnFullNameCorrectly() {
            // given
            Customer customer = buildActiveCustomer();

            // when
            String fullName = customer.getFullName();

            // then
            assertThat(fullName).isEqualTo("Juan Pérez");
        }
    }

    // validateIsOperational()
    // =========================================================
    @Nested
    @DisplayName("validateIsOperational()")
    class ValidateIsOperational {

        @Test
        @DisplayName("No debe lanzar excepción cuando el cliente está ACTIVE")
        void shouldNotThrowWhenCustomerIsActive() {
            // given
            Customer customer = buildActiveCustomer();

            // when - then
            assertThatNoException()
                    .isThrownBy(customer::validateIsOperational);
        }

        @Test
        @DisplayName("Debe lanzar CustomerBlockedException cuando el cliente está BLOCKED")
        void shouldThrowWhenCustomerIsBlocked() {
            // given
            Customer customer = buildBlockedCustomer();

            // when - then
            assertThatThrownBy(customer::validateIsOperational)
                    .isInstanceOf(CustomerBlockedException.class);
        }

        @Test
        @DisplayName("Debe lanzar CustomerInactiveException cuando el cliente está INACTIVE")
        void shouldThrowWhenCustomerIsInactive() {
            // given
            Customer customer = buildInactiveCustomer();

            // when - then
            assertThatThrownBy(customer::validateIsOperational)
                    .isInstanceOf(CustomerInactiveException.class);
        }
    }

    // updatePersonalInfo()
    // =========================================================
    @Nested
    @DisplayName("updatePersonalInfo()")
    class UpdatePersonalInfo {

        @Test
        @DisplayName("Debe actualizar los datos personales correctamente")
        void shouldUpdatePersonalInfoCorrectly() {
            // given
            Customer customer = buildActiveCustomer();

            // when
            customer.updatePersonalInfo("Juan Carlos", "Pérez López", "999888777");

            // then
            assertThat(customer.getFirstName()).isEqualTo("Juan Carlos");
            assertThat(customer.getLastName()).isEqualTo("Pérez López");
            assertThat(customer.getPhone()).isEqualTo("999888777");
        }
    }

    // deactivate()
    // =========================================================
    @Nested
    @DisplayName("deactivate()")
    class Deactivate {

        @Test
        @DisplayName("Debe cambiar el status a INACTIVE cuando el cliente está ACTIVE")
        void shouldDeactivateActiveCustomer() {
            // given
            Customer customer = buildActiveCustomer();

            // when
            customer.deactivate();

            // then
            assertThat(customer.getStatus()).isEqualTo(CustomerStatus.INACTIVE);
        }

        @Test
        @DisplayName("Debe lanzar excepción al desactivar un cliente ya INACTIVE")
        void shouldThrowWhenDeactivatingInactiveCustomer() {
            // given
            Customer customer = buildInactiveCustomer();

            // when - then
            assertThatThrownBy(customer::deactivate)
                    .isInstanceOf(CustomerInactiveException.class);
        }

        @Test
        @DisplayName("Debe lanzar excepción al desactivar un cliente BLOCKED")
        void shouldThrowWhenDeactivatingBlockedCustomer() {
            // given
            Customer customer = buildBlockedCustomer();

            // when - then
            assertThatThrownBy(customer::deactivate)
                    .isInstanceOf(CustomerBlockedException.class);
        }
    }
}
