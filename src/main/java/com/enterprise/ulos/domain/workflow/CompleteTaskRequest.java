package com.enterprise.ulos.domain.workflow;

import java.util.Map;

public record CompleteTaskRequest(
        Map<String, Object> variables
) {
}
