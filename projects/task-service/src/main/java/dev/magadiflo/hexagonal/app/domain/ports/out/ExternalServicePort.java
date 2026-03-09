package dev.magadiflo.hexagonal.app.domain.ports.out;

import dev.magadiflo.hexagonal.app.domain.models.AdditionalTaskInfo;

public interface ExternalServicePort {
    AdditionalTaskInfo getAdditionalTaskInfo(Long id);
}
