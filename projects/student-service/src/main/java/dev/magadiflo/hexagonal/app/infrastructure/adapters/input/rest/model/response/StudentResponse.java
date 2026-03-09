package dev.magadiflo.hexagonal.app.infrastructure.adapters.input.rest.model.response;

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
public class StudentResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private Integer age;
    private String address;
}
