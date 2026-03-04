package dev.magadiflo.banking.app.customer.infrastructure.adapter.input.rest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateCustomerRequest(@NotBlank(message = "El nombre es obligatorio")
                                    @Size(max = 100, message = "El nombre no puede superar los 100 caracteres")
                                    String firstName,

                                    @NotBlank(message = "El apellido es obligatorio")
                                    @Size(max = 100, message = "El apellido no puede superar los 100 caracteres")
                                    String lastName,

                                    @NotBlank(message = "El teléfono es obligatorio")
                                    @Size(max = 20, message = "El teléfono no puede superar los 20 caracteres")
                                    String phone) {
}
