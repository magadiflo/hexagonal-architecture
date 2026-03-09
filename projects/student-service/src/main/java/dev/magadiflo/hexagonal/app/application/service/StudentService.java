package dev.magadiflo.hexagonal.app.application.service;

import dev.magadiflo.hexagonal.app.application.ports.input.StudentServicePort;
import dev.magadiflo.hexagonal.app.application.ports.output.StudentPersistentPort;
import dev.magadiflo.hexagonal.app.domain.exception.StudentNotFoundException;
import dev.magadiflo.hexagonal.app.domain.model.Student;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class StudentService implements StudentServicePort {

    private final StudentPersistentPort studentPersistentPort;

    @Override
    public List<Student> findAllStudents() {
        return this.studentPersistentPort.findAllStudents();
    }

    @Override
    public Student findStudentById(Long id) {
        return this.studentPersistentPort.findStudentById(id)
                .orElseThrow(() -> new StudentNotFoundException("Error al buscar estudiante. No se encuentra con id: %s".formatted(id)));
    }

    @Override
    public Student saveStudent(Student student) {
        return this.studentPersistentPort.saveStudent(student);
    }

    @Override
    public Student updateStudent(Long id, Student student) {
        return this.studentPersistentPort.findStudentById(id)
                .map(studentDB -> {
                    studentDB.setFirstName(student.getFirstName());
                    studentDB.setLastName(student.getLastName());
                    studentDB.setAge(student.getAge());
                    studentDB.setAddress(student.getAddress());
                    return studentDB;
                })
                .map(this.studentPersistentPort::saveStudent)
                .orElseThrow(() -> new StudentNotFoundException("Error al actualizar estudiante. No se encuentra con id: %s".formatted(id)));
    }

    @Override
    public void deleteStudentById(Long id) {
        if (this.studentPersistentPort.findStudentById(id).isEmpty()) {
            throw new StudentNotFoundException("Error al eliminar estudiante. No se encuentra con id: %s".formatted(id));
        }
        this.studentPersistentPort.deleteStudentById(id);
    }
}
