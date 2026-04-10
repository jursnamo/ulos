package com.enterprise.ulos.domain.bpmn;

public record BpmnDeployRequest(
        String processKey,
        String processName,
        String resourceName,
        String bpmnXml,
        String deployedBy,
        String changeSummary
) {
}
