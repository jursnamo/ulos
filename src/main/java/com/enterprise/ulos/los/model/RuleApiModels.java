package com.enterprise.ulos.los.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public final class RuleApiModels {

    private RuleApiModels() {
    }

    public record ApprovalRuleRequest(
            String ruleId,
            String ruleName,
            String ruleType,
            String metricKey,
            String operator,
            BigDecimal thresholdValue,
            String actionRouting,
            String conditionExpression,
            String baseApprover,
            String escalatedApprover,
            String committeeApprover,
            String boardApprover,
            Boolean justificationRequired,
            Boolean active,
            String messageTemplate
    ) {
    }

    public record ApprovalRuleResponse(
            String ruleId,
            String ruleName,
            String ruleType,
            String metricKey,
            String operator,
            BigDecimal thresholdValue,
            String actionRouting,
            String conditionExpression,
            String baseApprover,
            String escalatedApprover,
            String committeeApprover,
            String boardApprover,
            Boolean justificationRequired,
            Boolean active,
            String messageTemplate,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
    }

    public record RuleEvaluationResponse(
            boolean hardStop,
            boolean warningTriggered,
            boolean deviationTriggered,
            boolean justificationRequired,
            String requiredApprovalTier,
            String baseApprover,
            String regionalApprover,
            String committeeApprover,
            String boardApprover,
            BigDecimal proposedExposure,
            BigDecimal availableLimit,
            BigDecimal debtToEquityRatio,
            BigDecimal collateralCoverage,
            BigDecimal currentRatio,
            List<RuleHit> hits
    ) {
    }

    public record RuleHit(
            String ruleId,
            String ruleName,
            String ruleType,
            String metricKey,
            String operator,
            BigDecimal thresholdValue,
            BigDecimal actualValue,
            String actionRouting,
            boolean justificationRequired,
            String message
    ) {
    }
}
