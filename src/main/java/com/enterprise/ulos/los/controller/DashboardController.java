package com.enterprise.ulos.los.controller;

import com.enterprise.ulos.los.model.WorkflowApiModels;
import com.enterprise.ulos.los.security.RequiresRoles;
import com.enterprise.ulos.los.service.ApplicationWorkspaceService;
import com.enterprise.ulos.los.service.CorporateLosWorkflowService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final ApplicationWorkspaceService applicationWorkspaceService;
    private final CorporateLosWorkflowService corporateLosWorkflowService;

    public DashboardController(
            ApplicationWorkspaceService applicationWorkspaceService,
            CorporateLosWorkflowService corporateLosWorkflowService
    ) {
        this.applicationWorkspaceService = applicationWorkspaceService;
        this.corporateLosWorkflowService = corporateLosWorkflowService;
    }

    @GetMapping("/summary")
    @RequiresRoles({"ADMIN", "ANALYST", "RISK", "COMMITTEE", "VIEWER"})
    public WorkflowApiModels.DashboardSummaryResponse summary() {
        return applicationWorkspaceService.buildDashboardSummary(corporateLosWorkflowService.openTaskCount());
    }
}
