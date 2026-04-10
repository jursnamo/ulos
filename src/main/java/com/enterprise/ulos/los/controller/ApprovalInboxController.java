package com.enterprise.ulos.los.controller;

import com.enterprise.ulos.los.model.WorkflowApiModels;
import com.enterprise.ulos.los.service.CorporateApprovalWorkflowService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/approvals")
@PreAuthorize("hasAnyRole('ADMIN','ANALYST','BRANCH_MANAGER','REGIONAL_HEAD','CREDIT_COMMITTEE','BOARD_OF_DIRECTORS')")
public class ApprovalInboxController {

    private final CorporateApprovalWorkflowService corporateApprovalWorkflowService;

    public ApprovalInboxController(CorporateApprovalWorkflowService corporateApprovalWorkflowService) {
        this.corporateApprovalWorkflowService = corporateApprovalWorkflowService;
    }

    @GetMapping("/tasks")
    public List<WorkflowApiModels.ApprovalTaskResponse> tasks() {
        return corporateApprovalWorkflowService.getTasks();
    }

    @PostMapping("/tasks/{taskId}/complete")
    public void complete(@PathVariable String taskId, @RequestBody WorkflowApiModels.TaskDecisionRequest request) {
        corporateApprovalWorkflowService.completeTask(taskId, request);
    }
}
