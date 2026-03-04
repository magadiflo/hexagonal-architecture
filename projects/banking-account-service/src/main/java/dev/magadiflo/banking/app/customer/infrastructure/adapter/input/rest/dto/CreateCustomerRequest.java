package dev.magadiflo.banking.app.customer.infrastructure.adapter.input.rest.dto;

import dev.magadiflo.banking.app.customer.domain.model.enums.DocumentType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateCustomerRequest(@NotBlank(message = "El número de documento es obligatorio")
                                    @Size(min = 8, max = 20, message = "El número de documento debe tener entre 8 y 20 caracteres")
                                    String documentNumber,

                                    @NotNull(message = "El tipo de documento es obligatorio")
                                    DocumentType documentType,

                                    @NotBlank(message = "El nombre es obligatorio")
                                    @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
                                    String firstName,

                                    @NotBlank(message = "El apellido es obligatorio")
                                    @Size(max = 100, message = "El apellido no puede superar los 100 caracteres")
                                    String lastName,

                                    @NotBlank(message = "El email es obligatorio")
                                    @Email(message = "El formato del email es inválido")
                                    String email,

                                    @NotBlank(message = "El teléfono es obligatorio")
                                    @Size(max = 20, message = "El teléfono no puede superar los 20 caracteres")
                                    String phone) {
}
