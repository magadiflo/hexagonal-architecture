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

    public void validateIsOperational() {
        if (this.status == CustomerStatus.BLOCKED) {
            throw new CustomerBlockedException(this.customerCode.value());
        }
        if (this.status == CustomerStatus.INACTIVE) {
            throw new CustomerInactiveException(this.customerCode.value());
        }
    }

    public void updatePersonalInfo(String firstName, String lastName, String phone) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
    }

    public void deactivate() {
        this.validateIsOperational();
        this.status = CustomerStatus.INACTIVE;
    }

    public String getFullName() {
        return String.format("%s %s", this.firstName, this.lastName);
    }
}
