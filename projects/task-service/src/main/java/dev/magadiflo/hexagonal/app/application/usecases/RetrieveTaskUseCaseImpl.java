package dev.magadiflo.hexagonal.app.application.usecases;

import dev.magadiflo.hexagonal.app.domain.models.Task;
import dev.magadiflo.hexagonal.app.domain.ports.in.RetrieveTaskUseCase;
import dev.magadiflo.hexagonal.app.domain.ports.out.TaskRepositoryPort;

import java.util.List;
import java.util.Optional;

public class RetrieveTaskUseCaseImpl implements RetrieveTaskUseCase {
    private final TaskRepositoryPort taskRepositoryPort;

    public RetrieveTaskUseCaseImpl(TaskRepositoryPort taskRepositoryPort) {
        this.taskRepositoryPort = taskRepositoryPort;
    }

    @Override
    public List<Task> getAllTasks() {
        return this.taskRepositoryPort.findAllTasks();
    }

    @Override
    public Optional<Task> getTask(Long id) {
        return this.taskRepositoryPort.findById(id);
    }
}
