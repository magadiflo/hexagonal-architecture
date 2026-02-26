package dev.magadiflo.banking.app.customer.domain.model;

import dev.magadiflo.banking.app.customer.domain.exception.CustomerBlockedException;
import dev.magadiflo.banking.app.customer.domain.exception.CustomerInactiveException;
import dev.magadiflo.banking.app.customer.domain.model.enums.CustomerStatus;
import dev.magadiflo.banking.app.customer.domain.model.enums.DocumentType;
import dev.magadiflo.banking.app.customer.domain.model.vo.CustomerCode;
import dev.magadiflo.banking.app.customer.domain.model.vo.CustomerId;
import dev.magadiflo.banking.app.customer.domain.model.vo.DocumentNumber;
import dev.magadiflo.banking.app.customer.domain.model.vo.Email;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Customer {

    // NOTA: Los campos createdAt y updatedAt NO se asignan manualmente en el dominio.
    // La responsabilidad de gestionarlos recae en la capa de infraestructura a través
    // de las anotaciones @CreationTimestamp y @UpdateTimestamp en CustomerEntity (JPA).
    // De esta forma, la fuente de verdad de las fechas es siempre la base de datos.

    private CustomerId id;
    private CustomerCode customerCode;
    private DocumentNumber documentNumber;
    private DocumentType documentType;
    private String firstName;
    private String lastName;
    private Email email;
    private String phone;
    private CustomerStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // FACTORY METHODS
    // =========================================================

    /**
     * Crea un nuevo cliente que aún NO ha sido persistido.
     * El id es null porque aún no ha sido asignado por la BD.
     * El status se inicializa en ACTIVE porque tod0 cliente nuevo
     * nace activo por regla de negocio.
     */
    public static Customer create(String customerCode,
                                  String documentNumber,
                                  DocumentType documentType,
                                  String firstName,
                                  String lastName,
                                  String email,
                                  String phone) {

        Customer customer = new Customer();
        customer.customerCode = new CustomerCode(customerCode);
        customer.documentNumber = new DocumentNumber(documentNumber);
        customer.documentType = documentType;
        customer.firstName = firstName;
        customer.lastName = lastName;
        customer.email = new Email(email);
        customer.phone = phone;
        customer.status = CustomerStatus.ACTIVE;

        return customer;
    }

    /**
     * Reconstituye un cliente existente desde la base de datos.
     * Usado por el mapper de infraestructura para convertir una
     * CustomerEntity en un Customer de dominio. Aquí sí se asigna
     * el id porque ya existe en la BD.
     */
    public static Customer reconstitute(Long id,
                                        String customerCode,
                                        String documentNumber,
                                        DocumentType documentType,
                                        String firstName,
                                        String lastName,
                                        String email,
                                        String phone,
                                        CustomerStatus status,
                                        LocalDateTime createdAt,
                                        LocalDateTime updatedAt) {

        Customer customer = new Customer();
        customer.id = new CustomerId(id);
        customer.customerCode = new CustomerCode(customerCode);
        customer.documentNumber = new DocumentNumber(documentNumber);
        customer.documentType = documentType;
        customer.firstName = firstName;
        customer.lastName = lastName;
        customer.email = new Email(email);
        customer.phone = phone;
        customer.status = status;
        customer.createdAt = createdAt;
        customer.updatedAt = updatedAt;

        return customer;
    }

    // COMPORTAMIENTO / REGLAS DE NEGOCIO
    // =========================================================

    /**
     * Verifica que el cliente puede realizar operaciones.
     * Lanza excepción si está bloqueado o inactivo.
     * Reutilizable desde cualquier caso de uso que lo requiera.
     */
    public void validateIsOperational() {
        if (this.status == CustomerStatus.BLOCKED) {
            throw new CustomerBlockedException(this.customerCode.value());
        }
        if (this.status == CustomerStatus.INACTIVE) {
            throw new CustomerInactiveException(this.customerCode.value());
        }
    }

    /**
     * Actualiza los datos personales del cliente.
     * Solo campos no sensibles son modificables.
     * El email y documento no se actualizan por política del banco.
     */
    public void updatePersonalInfo(String firstName, String lastName, String phone) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
    }

    /**
     * Desactiva el cliente (baja lógica).
     * En un banco jamás se eliminan registros físicamente.
     */
    public void deactivate() {
        this.validateIsOperational();
        this.status = CustomerStatus.INACTIVE;
    }

    /**
     * Retorna el nombre completo del cliente.
     */
    public String getFullName() {
        return String.format("%s %s", this.firstName, this.lastName);
    }
}
