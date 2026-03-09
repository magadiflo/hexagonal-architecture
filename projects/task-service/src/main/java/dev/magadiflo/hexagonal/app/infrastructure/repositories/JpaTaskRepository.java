package dev.magadiflo.hexagonal.app.infrastructure.repositories;

import dev.magadiflo.hexagonal.app.infrastructure.entities.TaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaTaskRepository extends JpaRepository<TaskEntity, Long> {
}
