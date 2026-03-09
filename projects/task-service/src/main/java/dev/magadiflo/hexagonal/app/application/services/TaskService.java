package dev.magadiflo.hexagonal.app.application.services;

import dev.magadiflo.hexagonal.app.domain.models.AdditionalTaskInfo;
import dev.magadiflo.hexagonal.app.domain.models.Task;
import dev.magadiflo.hexagonal.app.domain.ports.in.CreateTaskUseCase;
import dev.magadiflo.hexagonal.app.domain.ports.in.DeleteTaskUseCase;
import dev.magadiflo.hexagonal.app.domain.ports.in.GetAdditionalTaskInfoUseCase;
import dev.magadiflo.hexagonal.app.domain.ports.in.RetrieveTaskUseCase;
import dev.magadiflo.hexagonal.app.domain.ports.in.UpdateTaskUseCase;

import java.util.List;
import java.util.Optional;

public class TaskService implements CreateTaskUseCase, UpdateTaskUseCase, DeleteTaskUseCase, RetrieveTaskUseCase,
        GetAdditionalTaskInfoUseCase {

    private final CreateTaskUseCase createTaskUseCase;
    private final UpdateTaskUseCase updateTaskUseCase;
    private final DeleteTaskUseCase deleteTaskUseCase;
    private final RetrieveTaskUseCase retrieveTaskUseCase;
    private final GetAdditionalTaskInfoUseCase getAdditionalTaskInfoUseCase;

    public TaskService(CreateTaskUseCase createTaskUseCase, UpdateTaskUseCase updateTaskUseCase,
                       DeleteTaskUseCase deleteTaskUseCase, RetrieveTaskUseCase retrieveTaskUseCase,
                       GetAdditionalTaskInfoUseCase getAdditionalTaskInfoUseCase) {
        this.createTaskUseCase = createTaskUseCase;
        this.updateTaskUseCase = updateTaskUseCase;
        this.deleteTaskUseCase = deleteTaskUseCase;
        this.retrieveTaskUseCase = retrieveTaskUseCase;
        this.getAdditionalTaskInfoUseCase = getAdditionalTaskInfoUseCase;
    }

    @Override
    public Task createTask(Task task) {
        return this.createTaskUseCase.createTask(task);
    }

    @Override
    public Boolean deleteTask(Long id) {
        return this.deleteTaskUseCase.deleteTask(id);
    }

    @Override
    public AdditionalTaskInfo getAdditionalTaskInfo(Long id) {
        return this.getAdditionalTaskInfoUseCase.getAdditionalTaskInfo(id);
    }

    @Override
    public List<Task> getAllTasks() {
        return this.retrieveTaskUseCase.getAllTasks();
    }

    @Override
    public Optional<Task> getTask(Long id) {
        return this.retrieveTaskUseCase.getTask(id);
    }

    @Override
    public Optional<Task> updateTask(Long id, Task updateTask) {
        return this.updateTaskUseCase.updateTask(id, updateTask);
    }
}
