package com.enterprise.ulos.los.controller;

import com.enterprise.ulos.los.model.WorkflowApiModels;
import com.enterprise.ulos.los.security.RequiresRoles;
import com.enterprise.ulos.los.service.CorporateLosWorkflowService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/legacy-approvals")
public class ApprovalController {

    private final CorporateLosWorkflowService corporateLosWorkflowService;

    public ApprovalController(CorporateLosWorkflowService corporateLosWorkflowService) {
        this.corporateLosWorkflowService = corporateLosWorkflowService;
    }

    @GetMapping("/tasks")
    @RequiresRoles({"ADMIN", "ANALYST", "RISK", "COMMITTEE"})
    public List<WorkflowApiModels.ApprovalTaskResponse> tasks() {
        return corporateLosWorkflowService.tasks();
    }

    @PostMapping("/tasks/{taskId}/complete")
    @RequiresRoles({"ADMIN", "ANALYST", "RISK", "COMMITTEE"})
    public void complete(@PathVariable String taskId, @RequestBody WorkflowApiModels.TaskDecisionRequest request) {
        corporateLosWorkflowService.complete(taskId, request);
    }
}
