package com.enterprise.ulos.domain.bpmn;

public record BpmnXmlResponse(
        String processDefinitionKey,
        String processDefinitionId,
        Integer version,
        String resourceName,
        String bpmnXml,
        String deployedBy,
        String changeSummary
) {
}
