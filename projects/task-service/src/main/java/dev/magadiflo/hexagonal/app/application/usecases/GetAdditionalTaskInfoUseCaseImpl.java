package dev.magadiflo.hexagonal.app.application.usecases;

import dev.magadiflo.hexagonal.app.domain.models.AdditionalTaskInfo;
import dev.magadiflo.hexagonal.app.domain.ports.in.GetAdditionalTaskInfoUseCase;
import dev.magadiflo.hexagonal.app.domain.ports.out.ExternalServicePort;

public class GetAdditionalTaskInfoUseCaseImpl implements GetAdditionalTaskInfoUseCase {
    private final ExternalServicePort externalServicePort;

    public GetAdditionalTaskInfoUseCaseImpl(ExternalServicePort externalServicePort) {
        this.externalServicePort = externalServicePort;
    }

    @Override
    public AdditionalTaskInfo getAdditionalTaskInfo(Long id) {
        return this.externalServicePort.getAdditionalTaskInfo(id);
    }
}
