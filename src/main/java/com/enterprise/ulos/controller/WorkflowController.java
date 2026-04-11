package com.enterprise.ulos.controller;

import com.enterprise.ulos.domain.workflow.CompleteTaskRequest;
import com.enterprise.ulos.domain.workflow.StartLoanWorkflowRequest;
import com.enterprise.ulos.domain.workflow.WorkflowStartResponse;
import com.enterprise.ulos.domain.workflow.WorkflowTaskResponse;
import com.enterprise.ulos.los.security.RequiresRoles;
import com.enterprise.ulos.service.LoanWorkflowOrchestrationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/workflow")
public class WorkflowController {

    private final LoanWorkflowOrchestrationService orchestrationService;

    public WorkflowController(LoanWorkflowOrchestrationService orchestrationService) {
        this.orchestrationService = orchestrationService;
    }

    @PostMapping("/loan/start")
    @RequiresRoles({"ADMIN", "ANALYST"})
    public WorkflowStartResponse startLoanWorkflow(@RequestBody StartLoanWorkflowRequest request) {
        return orchestrationService.startLoanWorkflow(request);
    }

    @GetMapping("/tasks")
    @RequiresRoles({"ADMIN", "ANALYST", "RISK", "COMMITTEE"})
    public List<WorkflowTaskResponse> tasks() {
        return orchestrationService.getTasks();
    }

    @PostMapping("/tasks/{taskId}/complete")
    @RequiresRoles({"ADMIN", "ANALYST", "RISK", "COMMITTEE"})
    public void completeTask(@PathVariable String taskId, @RequestBody(required = false) CompleteTaskRequest request) {
        orchestrationService.completeTask(taskId, request);
    }
}
