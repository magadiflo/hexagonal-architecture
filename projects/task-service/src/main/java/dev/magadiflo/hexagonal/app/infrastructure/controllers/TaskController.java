package dev.magadiflo.hexagonal.app.infrastructure.controllers;

import dev.magadiflo.hexagonal.app.application.services.TaskService;
import dev.magadiflo.hexagonal.app.domain.models.AdditionalTaskInfo;
import dev.magadiflo.hexagonal.app.domain.models.Task;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(path = "/api/v1/tasks")
public class TaskController {
    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public ResponseEntity<List<Task>> getAllTasks() {
        return ResponseEntity.ok(this.taskService.getAllTasks());
    }

    @GetMapping(path = "/{taskId}")
    public ResponseEntity<Task> getTask(@PathVariable Long taskId) {
        return this.taskService.getTask(taskId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody Task task) {
        Task createdTask = this.taskService.createTask(task);
        URI uriTask = URI.create("/api/v1/tasks/" + createdTask.getId());
        return ResponseEntity.created(uriTask).body(createdTask);
    }

    @PutMapping(path = "/{taskId}")
    public ResponseEntity<Task> updateTask(@PathVariable Long taskId, @RequestBody Task updateTask) {
        return this.taskService.updateTask(taskId, updateTask)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping(path = "/{taskId}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long taskId) {
        Boolean deleted = this.taskService.deleteTask(taskId);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @GetMapping(path = "/{taskId}/additionalInfo")
    public ResponseEntity<AdditionalTaskInfo> getAdditionalTaskInfo(@PathVariable Long taskId) {
        AdditionalTaskInfo additionalTaskInfo = this.taskService.getAdditionalTaskInfo(taskId);
        return ResponseEntity.ok(additionalTaskInfo);
    }

}
