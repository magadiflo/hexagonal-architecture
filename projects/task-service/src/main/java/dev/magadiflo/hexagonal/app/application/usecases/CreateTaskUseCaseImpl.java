package dev.magadiflo.hexagonal.app.application.usecases;

import dev.magadiflo.hexagonal.app.domain.models.Task;
import dev.magadiflo.hexagonal.app.domain.ports.in.CreateTaskUseCase;
import dev.magadiflo.hexagonal.app.domain.ports.out.TaskRepositoryPort;

public class CreateTaskUseCaseImpl implements CreateTaskUseCase {
    private final TaskRepositoryPort taskRepositoryPort;

    public CreateTaskUseCaseImpl(TaskRepositoryPort taskRepositoryPort) {
        this.taskRepositoryPort = taskRepositoryPort;
    }

    @Override
    public Task createTask(Task task) {
        return this.taskRepositoryPort.save(task);
    }
}
