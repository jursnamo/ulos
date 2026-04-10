package com.enterprise.ulos.los.controller;

import com.enterprise.ulos.los.model.WorkflowApiModels;
import com.enterprise.ulos.los.service.CorporateApprovalWorkflowService;
import com.enterprise.ulos.los.service.CreditApplicationService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@PreAuthorize("hasAnyRole('ADMIN','RM','ANALYST','BRANCH_MANAGER','REGIONAL_HEAD','CREDIT_COMMITTEE','BOARD_OF_DIRECTORS','WORKFLOW_ADMIN')")
public class DashboardController {

    private final CreditApplicationService creditApplicationService;
    private final CorporateApprovalWorkflowService corporateApprovalWorkflowService;

    public DashboardController(
            CreditApplicationService creditApplicationService,
            CorporateApprovalWorkflowService corporateApprovalWorkflowService
    ) {
        this.creditApplicationService = creditApplicationService;
        this.corporateApprovalWorkflowService = corporateApprovalWorkflowService;
    }

    @GetMapping("/summary")
    public WorkflowApiModels.DashboardSummaryResponse summary() {
        return creditApplicationService.buildDashboardSummary(corporateApprovalWorkflowService.openTaskCount());
    }
}
