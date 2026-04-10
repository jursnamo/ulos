package com.enterprise.ulos.domain.bpmn;

public record BpmnDeployRequest(
        String processName,
        String resourceName,
        String bpmnXml
) {
}
