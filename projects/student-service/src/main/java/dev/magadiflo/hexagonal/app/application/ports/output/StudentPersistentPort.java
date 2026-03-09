package dev.magadiflo.hexagonal.app.application.ports.output;

import dev.magadiflo.hexagonal.app.domain.model.Student;

import java.util.List;
import java.util.Optional;

public interface StudentPersistentPort {
    List<Student> findAllStudents();

    Optional<Student> findStudentById(Long id);

    Student saveStudent(Student student);

    void deleteStudentById(Long id);
}
