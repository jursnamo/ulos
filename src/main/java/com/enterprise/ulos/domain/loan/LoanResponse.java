package com.enterprise.ulos.domain.loan;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record LoanResponse(
        String loanId,
        String customerId,
        String customerName,
        BigDecimal loanAmount,
        Integer tenorMonths,
        String businessKey,
        String processInstanceId,
        String processDefinitionKey,
        String status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
