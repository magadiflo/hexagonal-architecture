package dev.magadiflo.hexagonal.app.domain.ports.in;

public interface DeleteTaskUseCase {
    Boolean deleteTask(Long id);
}
