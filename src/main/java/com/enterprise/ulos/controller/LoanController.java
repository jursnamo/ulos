package com.enterprise.ulos.controller;

import com.enterprise.ulos.domain.loan.LoanResponse;
import com.enterprise.ulos.service.LoanService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/loans")
public class LoanController {

    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    @GetMapping
    public List<LoanResponse> getLoans() {
        return loanService.getLoans();
    }

    @GetMapping("/{loanId}")
    public LoanResponse getLoan(@PathVariable String loanId) {
        return loanService.getLoan(loanId);
    }
}
