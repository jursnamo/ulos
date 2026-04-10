package com.enterprise.ulos.domain.bpmn;

public record BpmnDeployResponse(
        String deploymentId,
        String deploymentName,
        String processDefinitionId,
        String processDefinitionKey,
        Integer processDefinitionVersion,
        String resourceName,
        String deployedBy,
        String changeSummary
) {
}
