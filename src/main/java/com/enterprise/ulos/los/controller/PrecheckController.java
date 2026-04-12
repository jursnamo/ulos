package com.enterprise.ulos.los.controller;

import com.enterprise.ulos.los.model.PrecheckApiModels;
import com.enterprise.ulos.los.security.RequiresRoles;
import com.enterprise.ulos.los.service.PrecheckService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/prechecks")
public class PrecheckController {

    private final PrecheckService precheckService;

    public PrecheckController(PrecheckService precheckService) {
        this.precheckService = precheckService;
    }

    @GetMapping
    @RequiresRoles({"ADMIN", "ANALYST", "RISK", "COMMITTEE", "VIEWER"})
    public PrecheckApiModels.PrecheckPageResponse list(
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "customerCif", required = false) String customerCif,
            @RequestParam(value = "q", required = false) String keyword,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size
    ) {
        return precheckService.listPage(type, customerCif, keyword, page, size);
    }

    @GetMapping("/{checkRef}")
    @RequiresRoles({"ADMIN", "ANALYST", "RISK", "COMMITTEE", "VIEWER"})
    public PrecheckApiModels.PrecheckResponse get(@PathVariable String checkRef) {
        return precheckService.getByCheckRef(checkRef);
    }

    @GetMapping("/source/latest-application")
    @RequiresRoles({"ADMIN", "ANALYST", "RISK", "COMMITTEE", "VIEWER"})
    public PrecheckApiModels.LatestApplicationSourceResponse latestSource(
            @RequestParam("type") String type,
            @RequestParam("customerCif") String customerCif
    ) {
        return precheckService.latestSource(type, customerCif);
    }

    @PostMapping("/request")
    @RequiresRoles({"ADMIN", "ANALYST", "RISK"})
    public PrecheckApiModels.PrecheckResponse requestNew(@RequestBody PrecheckApiModels.RequestNewPrecheckRequest request) {
        return precheckService.requestNew(request);
    }

    @PostMapping
    @RequiresRoles({"ADMIN", "ANALYST", "RISK"})
    public PrecheckApiModels.PrecheckResponse save(@RequestBody PrecheckApiModels.PrecheckRequest request) {
        return precheckService.save(request);
    }
}
