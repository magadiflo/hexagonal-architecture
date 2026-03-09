package dev.magadiflo.hexagonal.app.infrastructure.adapters.input.rest.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StudentCreateRequest {
    @NotBlank(message = "El campo firstName no puede estar vacío o ser nulo")
    private String firstName;

    @NotBlank(message = "El campo lastName no puede estar vacío o ser nulo")
    private String lastName;

    @NotNull(message = "El campo age no puede ser nulo")
    private Integer age;

    @NotBlank(message = "El campo address no puede estar vacío o ser nulo")
    private String address;
}
