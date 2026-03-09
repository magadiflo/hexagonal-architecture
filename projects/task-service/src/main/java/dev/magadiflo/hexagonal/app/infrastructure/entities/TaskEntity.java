package dev.magadiflo.hexagonal.app.infrastructure.entities;

import dev.magadiflo.hexagonal.app.domain.models.Task;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;

import java.time.LocalDateTime;

@Entity
public class TaskEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String description;
    private LocalDateTime creationDate;
    private Boolean completed;

    @PrePersist
    public void prePersist() {
        this.creationDate = LocalDateTime.now();
    }

    public TaskEntity() {
    }

    public TaskEntity(Long id, String title, String description, LocalDateTime creationDate, Boolean completed) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.creationDate = creationDate;
        this.completed = completed;
    }

    public static TaskEntity formDomainModel(Task task) {
        return new TaskEntity(task.getId(), task.getTitle(), task.getDescription(),
                task.getCreationDate(), task.getCompleted());
    }

    public Task toDomainModel() {
        return new Task(this.id, this.title, this.description, this.creationDate, this.completed);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public Boolean getCompleted() {
        return completed;
    }

    public void setCompleted(Boolean completed) {
        this.completed = completed;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TaskEntity{");
        sb.append("id=").append(id);
        sb.append(", title='").append(title).append('\'');
        sb.append(", description='").append(description).append('\'');
        sb.append(", creationDate=").append(creationDate);
        sb.append(", completed=").append(completed);
        sb.append('}');
        return sb.toString();
    }
}
