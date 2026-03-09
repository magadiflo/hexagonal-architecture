package dev.magadiflo.hexagonal.app.application.usecases;

import dev.magadiflo.hexagonal.app.domain.ports.in.DeleteTaskUseCase;
import dev.magadiflo.hexagonal.app.domain.ports.out.TaskRepositoryPort;

public class DeleteTaskUseCaseImpl implements DeleteTaskUseCase {
    private final TaskRepositoryPort taskRepositoryPort;

    public DeleteTaskUseCaseImpl(TaskRepositoryPort taskRepositoryPort) {
        this.taskRepositoryPort = taskRepositoryPort;
    }

    @Override
    public Boolean deleteTask(Long id) {
        return this.taskRepositoryPort.deleteById(id);
    }
}
