package dev.magadiflo.hexagonal.app.domain.ports.in;

import dev.magadiflo.hexagonal.app.domain.models.AdditionalTaskInfo;

public interface GetAdditionalTaskInfoUseCase {
    AdditionalTaskInfo getAdditionalTaskInfo(Long id);
}
