package dev.magadiflo.hexagonal.app.infrastructure.adapters.output.persistence;

import dev.magadiflo.hexagonal.app.application.ports.output.StudentPersistentPort;
import dev.magadiflo.hexagonal.app.domain.model.Student;
import dev.magadiflo.hexagonal.app.infrastructure.adapters.output.persistence.entity.StudentEntity;
import dev.magadiflo.hexagonal.app.infrastructure.adapters.output.persistence.mapper.StudentPersistentMapper;
import dev.magadiflo.hexagonal.app.infrastructure.adapters.output.persistence.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class StudentPersistentAdapter implements StudentPersistentPort {

    private final StudentRepository studentRepository;
    private final StudentPersistentMapper studentPersistentMapper;

    @Override
    @Transactional(readOnly = true)
    public List<Student> findAllStudents() {
        List<StudentEntity> studentEntityList = this.studentRepository.findAll();
        return this.studentPersistentMapper.toStudentList(studentEntityList);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Student> findStudentById(Long id) {
        return this.studentRepository.findById(id)
                .map(this.studentPersistentMapper::toStudent);
    }

    @Override
    @Transactional
    public Student saveStudent(Student student) {
        StudentEntity studentEntity = this.studentPersistentMapper.toStudentEntity(student);
        StudentEntity studentSaved = this.studentRepository.save(studentEntity);
        return this.studentPersistentMapper.toStudent(studentSaved);
    }

    @Override
    @Transactional
    public void deleteStudentById(Long id) {
        this.studentRepository.deleteById(id);
    }
}
