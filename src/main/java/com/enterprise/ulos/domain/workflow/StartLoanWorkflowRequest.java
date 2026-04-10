package com.enterprise.ulos.domain.workflow;

import java.math.BigDecimal;

public record StartLoanWorkflowRequest(
        String loanId,
        String customerId,
        String customerName,
        BigDecimal loanAmount,
        Integer tenorMonths
) {
}
