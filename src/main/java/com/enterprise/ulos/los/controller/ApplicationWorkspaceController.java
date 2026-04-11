package com.enterprise.ulos.los.controller;

import com.enterprise.ulos.los.model.ApplicationApiModels;
import com.enterprise.ulos.los.model.WorkflowApiModels;
import com.enterprise.ulos.los.security.RequiresRoles;
import com.enterprise.ulos.los.service.ApplicationWorkspaceService;
import com.enterprise.ulos.los.service.CorporateLosWorkflowService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/applications")
public class ApplicationWorkspaceController {

    private final ApplicationWorkspaceService applicationWorkspaceService;
    private final CorporateLosWorkflowService corporateLosWorkflowService;

    public ApplicationWorkspaceController(
            ApplicationWorkspaceService applicationWorkspaceService,
            CorporateLosWorkflowService corporateLosWorkflowService
    ) {
        this.applicationWorkspaceService = applicationWorkspaceService;
        this.corporateLosWorkflowService = corporateLosWorkflowService;
    }

    @GetMapping
    @RequiresRoles({"ADMIN", "ANALYST", "RISK", "COMMITTEE", "VIEWER"})
    public List<ApplicationApiModels.ApplicationSummaryResponse> list() {
        return applicationWorkspaceService.list();
    }

    @GetMapping("/{applicationId}")
    @RequiresRoles({"ADMIN", "ANALYST", "RISK", "COMMITTEE", "VIEWER"})
    public ApplicationApiModels.ApplicationWorkspaceResponse get(@PathVariable String applicationId) {
        return applicationWorkspaceService.get(applicationId);
    }

    @PostMapping
    @RequiresRoles({"ADMIN", "ANALYST"})
    public ApplicationApiModels.ApplicationWorkspaceResponse save(@RequestBody ApplicationApiModels.ApplicationWorkspaceRequest request) {
        return applicationWorkspaceService.saveDraft(request);
    }

    @PostMapping("/{applicationId}/submit")
    @RequiresRoles({"ADMIN", "ANALYST"})
    public WorkflowApiModels.WorkflowLaunchResponse submit(@PathVariable String applicationId) {
        return corporateLosWorkflowService.launch(applicationId);
    }
}
