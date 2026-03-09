package dev.magadiflo.hexagonal.app.application.usecases;

import dev.magadiflo.hexagonal.app.domain.models.Task;
import dev.magadiflo.hexagonal.app.domain.ports.in.UpdateTaskUseCase;
import dev.magadiflo.hexagonal.app.domain.ports.out.TaskRepositoryPort;

import java.util.Optional;

public class UpdateTaskUseCaseImpl implements UpdateTaskUseCase {
    private final TaskRepositoryPort taskRepositoryPort;

    public UpdateTaskUseCaseImpl(TaskRepositoryPort taskRepositoryPort) {
        this.taskRepositoryPort = taskRepositoryPort;
    }

    @Override
    public Optional<Task> updateTask(Long id, Task updateTask) {
        return this.taskRepositoryPort.findById(id)
                .map(taskDB -> {
                    taskDB.setTitle(updateTask.getTitle());
                    taskDB.setDescription(updateTask.getDescription());
                    return this.taskRepositoryPort.save(taskDB);
                });
    }
}
