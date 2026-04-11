package com.enterprise.ulos.los.controller;

import com.enterprise.ulos.los.model.CustomerApiModels;
import com.enterprise.ulos.los.security.RequiresRoles;
import com.enterprise.ulos.los.service.CustomerPortfolioService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerPortfolioController {

    private final CustomerPortfolioService customerPortfolioService;

    public CustomerPortfolioController(CustomerPortfolioService customerPortfolioService) {
        this.customerPortfolioService = customerPortfolioService;
    }

    @GetMapping
    @RequiresRoles({"ADMIN", "ANALYST", "RISK", "COMMITTEE", "VIEWER"})
    public List<CustomerApiModels.CustomerSummaryResponse> list() {
        return customerPortfolioService.list();
    }

    @GetMapping("/page")
    @RequiresRoles({"ADMIN", "ANALYST", "RISK", "COMMITTEE", "VIEWER"})
    public CustomerApiModels.CustomerSummaryPageResponse listPage(
            @RequestParam(value = "q", required = false) String keyword,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size
    ) {
        return customerPortfolioService.listPage(keyword, page, size);
    }

    @GetMapping("/{cifNumber}")
    @RequiresRoles({"ADMIN", "ANALYST", "RISK", "COMMITTEE", "VIEWER"})
    public CustomerApiModels.CustomerDetailResponse get(@PathVariable String cifNumber) {
        return customerPortfolioService.getByCif(cifNumber);
    }

    @PostMapping
    @RequiresRoles({"ADMIN", "ANALYST"})
    public CustomerApiModels.CustomerDetailResponse save(@RequestBody CustomerApiModels.CustomerRequest request) {
        return customerPortfolioService.save(request);
    }
}
