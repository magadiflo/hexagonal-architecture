package dev.magadiflo.hexagonal.app.infrastructure.adapters.output.persistence.repository;

import dev.magadiflo.hexagonal.app.infrastructure.adapters.output.persistence.entity.StudentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<StudentEntity, Long> {
}
