package dev.magadiflo.hexagonal.app.application.ports.input;

import dev.magadiflo.hexagonal.app.domain.model.Student;

import java.util.List;

public interface StudentServicePort {
    List<Student> findAllStudents();

    Student findStudentById(Long id);

    Student saveStudent(Student student);

    Student updateStudent(Long id, Student student);

    void deleteStudentById(Long id);
}
