package com.enterprise.ulos.domain.workflow;

public record WorkflowStartResponse(
        String processInstanceId,
        String processDefinitionKey,
        String businessKey
) {
}
