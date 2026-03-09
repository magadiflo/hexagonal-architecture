package dev.magadiflo.hexagonal.app.infrastructure.adapters.input.rest;

import dev.magadiflo.hexagonal.app.application.ports.input.StudentServicePort;
import dev.magadiflo.hexagonal.app.domain.model.Student;
import dev.magadiflo.hexagonal.app.infrastructure.adapters.input.rest.mapper.StudentRestMapper;
import dev.magadiflo.hexagonal.app.infrastructure.adapters.input.rest.model.request.StudentCreateRequest;
import dev.magadiflo.hexagonal.app.infrastructure.adapters.input.rest.model.response.StudentResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/api/v1/students")
public class StudentRestAdapter {

    private final StudentServicePort studentServicePort;
    private final StudentRestMapper studentRestMapper;

    @GetMapping
    public ResponseEntity<List<StudentResponse>> findAllStudents() {
        List<StudentResponse> studentResponses = this.studentRestMapper.toStudentResponseList(this.studentServicePort.findAllStudents());
        return ResponseEntity.ok(studentResponses);
    }

    @GetMapping(path = "/{studentId}")
    public ResponseEntity<StudentResponse> findStudent(@PathVariable Long studentId) {
        Student student = this.studentServicePort.findStudentById(studentId);
        return ResponseEntity.ok(this.studentRestMapper.toStudentResponse(student));
    }

    @PostMapping
    public ResponseEntity<StudentResponse> saveStudent(@Valid @RequestBody StudentCreateRequest studentCreateRequest) {
        Student student = this.studentRestMapper.toStudent(studentCreateRequest);
        Student studentDB = this.studentServicePort.saveStudent(student);
        URI uri = URI.create("/api/v1/students/" + studentDB.getId());
        return ResponseEntity.created(uri).body(this.studentRestMapper.toStudentResponse(studentDB));
    }

    /**
     * Para este caso particular, el dto StudentCreateRequest, será el mismo que usamos en el endpoint de save, pero
     * podríamos crear un dto exclusivo para este actualizar, todo va a depender de qué campos consideramos
     * sean actualizables.
     */
    @PutMapping(path = "/{studentId}")
    public ResponseEntity<StudentResponse> updateStudent(@Valid @RequestBody StudentCreateRequest studentCreateRequest, @PathVariable Long studentId) {
        Student student = this.studentRestMapper.toStudent(studentCreateRequest);
        Student studentDB = this.studentServicePort.updateStudent(studentId, student);
        return ResponseEntity.ok(this.studentRestMapper.toStudentResponse(studentDB));
    }

    @DeleteMapping(path = "/{studentId}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long studentId) {
        this.studentServicePort.deleteStudentById(studentId);
        return ResponseEntity.noContent().build();
    }
}
