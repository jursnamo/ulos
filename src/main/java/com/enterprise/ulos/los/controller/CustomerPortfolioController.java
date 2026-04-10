package com.enterprise.ulos.los.controller;

import com.enterprise.ulos.los.model.CustomerApiModels;
import com.enterprise.ulos.los.service.CustomerPortfolioService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
@PreAuthorize("hasAnyRole('ADMIN','RM','ANALYST','BRANCH_MANAGER','REGIONAL_HEAD','CREDIT_COMMITTEE','BOARD_OF_DIRECTORS')")
public class CustomerPortfolioController {

    private final CustomerPortfolioService customerPortfolioService;

    public CustomerPortfolioController(CustomerPortfolioService customerPortfolioService) {
        this.customerPortfolioService = customerPortfolioService;
    }

    @GetMapping
    public List<CustomerApiModels.CustomerSummaryResponse> list() {
        return customerPortfolioService.list();
    }

    @GetMapping("/{cifNumber}")
    public CustomerApiModels.CustomerPortfolioResponse get(@PathVariable String cifNumber) {
        return customerPortfolioService.get(cifNumber);
    }

    @PostMapping
    public CustomerApiModels.CustomerPortfolioResponse save(@RequestBody CustomerApiModels.CustomerPortfolioRequest request) {
        return customerPortfolioService.save(request);
    }
}
