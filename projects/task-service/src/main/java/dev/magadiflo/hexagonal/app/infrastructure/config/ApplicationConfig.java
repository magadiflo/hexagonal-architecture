package dev.magadiflo.hexagonal.app.infrastructure.config;

import dev.magadiflo.hexagonal.app.application.services.TaskService;
import dev.magadiflo.hexagonal.app.application.usecases.CreateTaskUseCaseImpl;
import dev.magadiflo.hexagonal.app.application.usecases.DeleteTaskUseCaseImpl;
import dev.magadiflo.hexagonal.app.application.usecases.GetAdditionalTaskInfoUseCaseImpl;
import dev.magadiflo.hexagonal.app.application.usecases.RetrieveTaskUseCaseImpl;
import dev.magadiflo.hexagonal.app.application.usecases.UpdateTaskUseCaseImpl;
import dev.magadiflo.hexagonal.app.domain.ports.in.GetAdditionalTaskInfoUseCase;
import dev.magadiflo.hexagonal.app.domain.ports.out.ExternalServicePort;
import dev.magadiflo.hexagonal.app.domain.ports.out.TaskRepositoryPort;
import dev.magadiflo.hexagonal.app.infrastructure.adapters.ExternalServiceAdapter;
import dev.magadiflo.hexagonal.app.infrastructure.repositories.JpaTaskRepositoryAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {
    @Bean
    public TaskService taskService(TaskRepositoryPort taskRepositoryPort,
                                   GetAdditionalTaskInfoUseCase getAdditionalTaskInfoUseCase) {
        return new TaskService(new CreateTaskUseCaseImpl(taskRepositoryPort),
                new UpdateTaskUseCaseImpl(taskRepositoryPort),
                new DeleteTaskUseCaseImpl(taskRepositoryPort),
                new RetrieveTaskUseCaseImpl(taskRepositoryPort),
                getAdditionalTaskInfoUseCase);
    }

    @Bean
    public TaskRepositoryPort taskRepositoryPort(JpaTaskRepositoryAdapter jpaTaskRepositoryAdapter) {
        return jpaTaskRepositoryAdapter;
    }

    @Bean
    public GetAdditionalTaskInfoUseCase getAdditionalTaskInfoUseCase(ExternalServicePort externalServicePort) {
        return new GetAdditionalTaskInfoUseCaseImpl(externalServicePort);
    }

    @Bean
    public ExternalServicePort externalServicePort() {
        return new ExternalServiceAdapter();
    }
}
