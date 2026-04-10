package com.enterprise.ulos.los.controller;

import com.enterprise.ulos.los.model.ApplicationApiModels;
import com.enterprise.ulos.los.model.WorkflowApiModels;
import com.enterprise.ulos.los.service.CorporateApprovalWorkflowService;
import com.enterprise.ulos.los.service.CreditApplicationService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/applications")
@PreAuthorize("hasAnyRole('ADMIN','RM','ANALYST','BRANCH_MANAGER','REGIONAL_HEAD','CREDIT_COMMITTEE','BOARD_OF_DIRECTORS')")
public class CreditApplicationController {

    private final CreditApplicationService creditApplicationService;
    private final CorporateApprovalWorkflowService corporateApprovalWorkflowService;

    public CreditApplicationController(
            CreditApplicationService creditApplicationService,
            CorporateApprovalWorkflowService corporateApprovalWorkflowService
    ) {
        this.creditApplicationService = creditApplicationService;
        this.corporateApprovalWorkflowService = corporateApprovalWorkflowService;
    }

    @GetMapping
    public List<ApplicationApiModels.CreditApplicationResponse> list() {
        return creditApplicationService.list();
    }

    @GetMapping("/{applicationId}")
    public ApplicationApiModels.CreditApplicationResponse get(@PathVariable String applicationId) {
        return creditApplicationService.get(applicationId);
    }

    @PostMapping
    public ApplicationApiModels.CreditApplicationResponse saveDraft(@RequestBody ApplicationApiModels.CreditApplicationRequest request) {
        return creditApplicationService.saveDraft(request);
    }

    @PostMapping("/{applicationId}/submit")
    public WorkflowApiModels.WorkflowLaunchResponse submit(@PathVariable String applicationId) {
        return corporateApprovalWorkflowService.launch(applicationId);
    }
}
