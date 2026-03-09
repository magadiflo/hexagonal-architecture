package dev.magadiflo.hexagonal.app.infrastructure.adapters.input.rest.mapper;

import dev.magadiflo.hexagonal.app.domain.model.Student;
import dev.magadiflo.hexagonal.app.infrastructure.adapters.input.rest.model.request.StudentCreateRequest;
import dev.magadiflo.hexagonal.app.infrastructure.adapters.input.rest.model.response.StudentResponse;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StudentRestMapper {
    Student toStudent(StudentCreateRequest request);

    StudentResponse toStudentResponse(Student student);

    List<StudentResponse> toStudentResponseList(List<Student> students);
}
