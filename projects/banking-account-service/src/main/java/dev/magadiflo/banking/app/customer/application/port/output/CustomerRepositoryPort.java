package dev.magadiflo.banking.app.customer.application.port.output;

import dev.magadiflo.banking.app.customer.domain.model.Customer;

import java.util.List;
import java.util.Optional;

public interface CustomerRepositoryPort {
    // Retorna todos los clientes registrados
    List<Customer> findAll();

    // Busca un cliente por su id (PK)
    Optional<Customer> findById(Long customerId);

    // Busca un cliente por su referencia externa (la que viaja en la API)
    Optional<Customer> findByCustomerCode(String customerCode);

    // Persiste un cliente nuevo o actualiza uno existente
    Customer save(Customer customer);

    // Verifica si ya existe un cliente con ese número de documento
    boolean existsByDocumentNumber(String documentNumber);

    // Verifica si ya existe un cliente con ese email
    boolean existsByEmail(String email);
}
