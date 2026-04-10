package com.enterprise.ulos.los.model;

import java.time.LocalDateTime;

public final class BpmnApiModels {

    private BpmnApiModels() {
    }

    public record BpmnVersionResponse(
            Long id,
            String processKey,
            String processName,
            String resourceName,
            String processDefinitionId,
            String deploymentId,
            Integer version,
            boolean active,
            String deployedBy,
            String changeSummary,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
    }
}
