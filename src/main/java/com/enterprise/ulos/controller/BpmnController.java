package com.enterprise.ulos.controller;

import com.enterprise.ulos.domain.bpmn.BpmnDeployRequest;
import com.enterprise.ulos.domain.bpmn.BpmnDeployResponse;
import com.enterprise.ulos.domain.bpmn.BpmnXmlResponse;
import com.enterprise.ulos.los.model.BpmnApiModels;
import com.enterprise.ulos.los.security.RequiresRoles;
import com.enterprise.ulos.service.BpmnDesignerService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/bpmn")
public class BpmnController {

    private final BpmnDesignerService bpmnDesignerService;

    public BpmnController(BpmnDesignerService bpmnDesignerService) {
        this.bpmnDesignerService = bpmnDesignerService;
    }

    @GetMapping
    @RequiresRoles({"ADMIN", "ANALYST", "RISK", "COMMITTEE", "VIEWER"})
    public List<BpmnApiModels.BpmnVersionResponse> listVersions() {
        return bpmnDesignerService.listVersions();
    }

    @GetMapping("/{processKey}")
    @RequiresRoles({"ADMIN", "ANALYST", "RISK", "COMMITTEE", "VIEWER"})
    public BpmnXmlResponse getProcessXml(@PathVariable String processKey) {
        return bpmnDesignerService.getProcessXml(processKey);
    }

    @PostMapping("/deploy")
    @RequiresRoles({"ADMIN"})
    public BpmnDeployResponse deploy(@RequestBody BpmnDeployRequest request) {
        return bpmnDesignerService.deployProcess(request);
    }
}
