package com.enterprise.ulos.los.controller;

import com.enterprise.ulos.los.model.RuleApiModels;
import com.enterprise.ulos.los.service.ApprovalRuleService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/rules")
@PreAuthorize("hasAnyRole('ADMIN','WORKFLOW_ADMIN','ANALYST')")
public class ApprovalRuleController {

    private final ApprovalRuleService approvalRuleService;

    public ApprovalRuleController(ApprovalRuleService approvalRuleService) {
        this.approvalRuleService = approvalRuleService;
    }

    @GetMapping
    public List<RuleApiModels.ApprovalRuleResponse> list() {
        return approvalRuleService.list();
    }

    @PostMapping
    public RuleApiModels.ApprovalRuleResponse save(@RequestBody RuleApiModels.ApprovalRuleRequest request) {
        return approvalRuleService.save(request);
    }
}
