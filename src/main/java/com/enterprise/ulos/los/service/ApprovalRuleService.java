package com.enterprise.ulos.los.service;

import com.enterprise.ulos.los.entity.ApprovalRuleEntity;
import com.enterprise.ulos.los.model.RuleApiModels;
import com.enterprise.ulos.los.repository.ApprovalRuleRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

@Service
@Transactional
public class ApprovalRuleService {

    private final ApprovalRuleRepository approvalRuleRepository;

    public ApprovalRuleService(ApprovalRuleRepository approvalRuleRepository) {
        this.approvalRuleRepository = approvalRuleRepository;
    }

    public RuleApiModels.ApprovalRuleResponse save(RuleApiModels.ApprovalRuleRequest request) {
        validateRuleRequest(request);

        String ruleId = request.ruleId() == null || request.ruleId().isBlank()
                ? "RULE-" + String.format("%03d", approvalRuleRepository.count() + 1)
                : request.ruleId().trim();

        ApprovalRuleEntity entity = approvalRuleRepository.findById(ruleId)
                .orElseGet(ApprovalRuleEntity::new);

        entity.setRuleId(ruleId);
        entity.setRuleName(request.ruleName().trim());
        entity.setRuleType(normalize(request.ruleType()));
        entity.setMetricKey(normalize(request.metricKey()));
        entity.setOperator(normalize(request.operator()));
        entity.setThresholdValue(request.thresholdValue());
        entity.setActionRouting(normalize(request.actionRouting()));
        entity.setConditionExpression(request.conditionExpression());
        entity.setBaseApprover(request.baseApprover());
        entity.setEscalatedApprover(request.escalatedApprover());
        entity.setCommitteeApprover(request.committeeApprover());
        entity.setBoardApprover(request.boardApprover());
        entity.setJustificationRequired(Boolean.TRUE.equals(request.justificationRequired()));
        entity.setActive(request.active() == null || request.active());
        entity.setMessageTemplate(request.messageTemplate());

        return toResponse(approvalRuleRepository.save(entity));
    }

    @Transactional(readOnly = true)
    public List<RuleApiModels.ApprovalRuleResponse> list() {
        return approvalRuleRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(ApprovalRuleEntity::getRuleId))
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public RuleApiModels.RuleEvaluationResponse evaluate(
            BigDecimal proposedExposure,
            BigDecimal availableLimit,
            BigDecimal debtToEquityRatio,
            BigDecimal collateralCoverage,
            BigDecimal currentRatio
    ) {
        List<ApprovalRuleEntity> activeRules = approvalRuleRepository.findByActiveTrueOrderByRuleIdAsc();
        List<RuleApiModels.RuleHit> hits = new ArrayList<>();

        boolean hardStop = false;
        boolean warningTriggered = false;
        boolean deviationTriggered = false;
        boolean justificationRequired = false;
        String requiredApprovalTier = "BRANCH_MANAGER";
        String baseApprover = "BRANCH_MANAGER";
        String regionalApprover = "REGIONAL_HEAD";
        String committeeApprover = "CREDIT_COMMITTEE";
        String boardApprover = "BOARD_OF_DIRECTORS";

        for (ApprovalRuleEntity rule : activeRules) {
            BigDecimal actualValue = resolveMetric(rule.getMetricKey(), proposedExposure, availableLimit, debtToEquityRatio, collateralCoverage, currentRatio);
            if (!matches(actualValue, rule.getOperator(), rule.getThresholdValue())) {
                continue;
            }

            String ruleType = normalize(rule.getRuleType());
            hardStop = hardStop || "HARD_STOP".equals(ruleType);
            warningTriggered = warningTriggered || "WARNING".equals(ruleType);
            deviationTriggered = deviationTriggered || "DEVIATION".equals(ruleType);
            justificationRequired = justificationRequired || rule.isJustificationRequired();
            requiredApprovalTier = higherTier(requiredApprovalTier, normalize(rule.getActionRouting()));

            if (rule.getBaseApprover() != null && !rule.getBaseApprover().isBlank()) {
                baseApprover = rule.getBaseApprover();
            }
            if (rule.getEscalatedApprover() != null && !rule.getEscalatedApprover().isBlank()) {
                regionalApprover = rule.getEscalatedApprover();
            }
            if (rule.getCommitteeApprover() != null && !rule.getCommitteeApprover().isBlank()) {
                committeeApprover = rule.getCommitteeApprover();
            }
            if (rule.getBoardApprover() != null && !rule.getBoardApprover().isBlank()) {
                boardApprover = rule.getBoardApprover();
            }

            hits.add(new RuleApiModels.RuleHit(
                    rule.getRuleId(),
                    rule.getRuleName(),
                    ruleType,
                    normalize(rule.getMetricKey()),
                    normalize(rule.getOperator()),
                    rule.getThresholdValue(),
                    actualValue,
                    normalize(rule.getActionRouting()),
                    rule.isJustificationRequired(),
                    renderMessage(rule, actualValue)
            ));
        }

        return new RuleApiModels.RuleEvaluationResponse(
                hardStop,
                warningTriggered,
                deviationTriggered,
                justificationRequired,
                hardStop ? "HARD_STOP" : requiredApprovalTier,
                baseApprover,
                regionalApprover,
                committeeApprover,
                boardApprover,
                safe(proposedExposure),
                safe(availableLimit),
                safe(debtToEquityRatio),
                safe(collateralCoverage),
                safe(currentRatio),
                hits
        );
    }

    private String renderMessage(ApprovalRuleEntity rule, BigDecimal actualValue) {
        if (rule.getMessageTemplate() != null && !rule.getMessageTemplate().isBlank()) {
            return rule.getMessageTemplate()
                    .replace("{actual}", safe(actualValue).toPlainString())
                    .replace("{threshold}", safe(rule.getThresholdValue()).toPlainString());
        }
        return "%s triggered: %s %s %s".formatted(
                rule.getRuleName(),
                normalize(rule.getMetricKey()),
                normalize(rule.getOperator()),
                safe(rule.getThresholdValue()).toPlainString()
        );
    }

    private BigDecimal resolveMetric(
            String metricKey,
            BigDecimal proposedExposure,
            BigDecimal availableLimit,
            BigDecimal debtToEquityRatio,
            BigDecimal collateralCoverage,
            BigDecimal currentRatio
    ) {
        return switch (normalize(metricKey)) {
            case "PROPOSED_EXPOSURE" -> safe(proposedExposure);
            case "AVAILABLE_LIMIT" -> safe(availableLimit);
            case "DER" -> safe(debtToEquityRatio);
            case "COLLATERAL_COVERAGE" -> safe(collateralCoverage);
            case "CURRENT_RATIO" -> safe(currentRatio);
            default -> BigDecimal.ZERO;
        };
    }

    private boolean matches(BigDecimal actualValue, String operator, BigDecimal thresholdValue) {
        int comparison = safe(actualValue).compareTo(safe(thresholdValue));
        return switch (normalize(operator)) {
            case "GT" -> comparison > 0;
            case "GTE" -> comparison >= 0;
            case "LT" -> comparison < 0;
            case "LTE" -> comparison <= 0;
            case "EQ" -> comparison == 0;
            default -> false;
        };
    }

    private String higherTier(String current, String candidate) {
        return tierRank(candidate) > tierRank(current) ? candidate : current;
    }

    private int tierRank(String tier) {
        return switch (normalize(tier)) {
            case "REGIONAL_HEAD" -> 2;
            case "CREDIT_COMMITTEE" -> 3;
            case "BOARD_OF_DIRECTORS" -> 4;
            case "BRANCH_MANAGER" -> 1;
            default -> 0;
        };
    }

    private RuleApiModels.ApprovalRuleResponse toResponse(ApprovalRuleEntity entity) {
        return new RuleApiModels.ApprovalRuleResponse(
                entity.getRuleId(),
                entity.getRuleName(),
                entity.getRuleType(),
                entity.getMetricKey(),
                entity.getOperator(),
                entity.getThresholdValue(),
                entity.getActionRouting(),
                entity.getConditionExpression(),
                entity.getBaseApprover(),
                entity.getEscalatedApprover(),
                entity.getCommitteeApprover(),
                entity.getBoardApprover(),
                entity.isJustificationRequired(),
                entity.isActive(),
                entity.getMessageTemplate(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    private void validateRuleRequest(RuleApiModels.ApprovalRuleRequest request) {
        if (request == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Rule request is required");
        }
        if (isBlank(request.ruleName()) || isBlank(request.ruleType()) || isBlank(request.metricKey())
                || isBlank(request.operator()) || request.thresholdValue() == null || isBlank(request.actionRouting())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Rule name, type, metric, operator, threshold, and routing are required");
        }
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().toUpperCase(Locale.ROOT);
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private BigDecimal safe(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }
}
