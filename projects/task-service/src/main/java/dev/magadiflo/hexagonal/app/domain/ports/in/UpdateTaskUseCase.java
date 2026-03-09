package dev.magadiflo.hexagonal.app.domain.ports.in;

import dev.magadiflo.hexagonal.app.domain.models.Task;

import java.util.Optional;

public interface UpdateTaskUseCase {
    Optional<Task> updateTask(Long id, Task updateTask);
}
