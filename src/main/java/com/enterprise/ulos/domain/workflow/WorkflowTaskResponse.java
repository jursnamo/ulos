package com.enterprise.ulos.domain.workflow;

public record WorkflowTaskResponse(
        String taskId,
        String taskName,
        String assignee,
        String processInstanceId,
        String processDefinitionId
) {
}
