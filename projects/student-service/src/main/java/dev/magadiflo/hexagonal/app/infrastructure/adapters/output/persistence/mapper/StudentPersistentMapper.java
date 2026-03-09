package dev.magadiflo.hexagonal.app.infrastructure.adapters.output.persistence.mapper;

import dev.magadiflo.hexagonal.app.domain.model.Student;
import dev.magadiflo.hexagonal.app.infrastructure.adapters.output.persistence.entity.StudentEntity;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface StudentPersistentMapper {
    StudentEntity toStudentEntity(Student student);

    Student toStudent(StudentEntity entity);

    List<Student> toStudentList(List<StudentEntity> studentEntityList);
}
