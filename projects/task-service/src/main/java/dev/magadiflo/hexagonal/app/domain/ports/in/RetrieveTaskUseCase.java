package dev.magadiflo.hexagonal.app.domain.ports.in;

import dev.magadiflo.hexagonal.app.domain.models.Task;

import java.util.List;
import java.util.Optional;

public interface RetrieveTaskUseCase {
    List<Task> getAllTasks();

    Optional<Task> getTask(Long id);
}
