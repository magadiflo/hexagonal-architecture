package dev.magadiflo.hexagonal.app.domain.ports.in;

import dev.magadiflo.hexagonal.app.domain.models.Task;

public interface CreateTaskUseCase {
    Task createTask(Task task);
}
