package com.enterprise.ulos.los.controller;

import com.enterprise.ulos.los.model.MasterDataApiModels;
import com.enterprise.ulos.los.security.RequiresRoles;
import com.enterprise.ulos.los.service.MasterDataService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/master")
public class MasterDataController {

    private final MasterDataService masterDataService;

    public MasterDataController(MasterDataService masterDataService) {
        this.masterDataService = masterDataService;
    }

    @GetMapping("/products")
    @RequiresRoles({"ADMIN", "ANALYST", "RISK", "COMMITTEE", "VIEWER"})
    public List<MasterDataApiModels.ProductResponse> products() {
        return masterDataService.products();
    }

    @GetMapping("/facilities")
    @RequiresRoles({"ADMIN", "ANALYST", "RISK", "COMMITTEE", "VIEWER"})
    public List<MasterDataApiModels.FacilityResponse> facilities() {
        return masterDataService.facilities();
    }

    @GetMapping("/portfolio-lookups")
    public List<MasterDataApiModels.PortfolioLookupResponse> portfolioLookups(
            @RequestParam(value = "masterType", required = false) String masterType,
            @RequestParam(value = "includeInactive", required = false, defaultValue = "false") boolean includeInactive
    ) {
        return masterDataService.portfolioLookups(masterType, includeInactive);
    }

    @GetMapping("/portfolio-lookups/page")
    public MasterDataApiModels.PortfolioLookupPageResponse portfolioLookupsPage(
            @RequestParam(value = "masterType", required = false) String masterType,
            @RequestParam(value = "q", required = false) String keyword,
            @RequestParam(value = "includeInactive", required = false, defaultValue = "false") boolean includeInactive,
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
            @RequestParam(value = "size", required = false, defaultValue = "50") int size
    ) {
        return masterDataService.portfolioLookupsPage(masterType, includeInactive, keyword, page, size);
    }

    @PostMapping("/portfolio-lookups")
    public MasterDataApiModels.PortfolioLookupResponse upsertPortfolioLookup(
            @RequestBody MasterDataApiModels.PortfolioLookupUpsertRequest request
    ) {
        return masterDataService.upsertPortfolioLookup(request);
    }

    @DeleteMapping("/portfolio-lookups/{masterType}/{code}")
    public void deletePortfolioLookup(@PathVariable String masterType, @PathVariable String code) {
        masterDataService.deletePortfolioLookup(masterType, code);
    }
}
