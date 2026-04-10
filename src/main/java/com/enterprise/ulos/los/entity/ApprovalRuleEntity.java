package com.enterprise.ulos.los.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "approval_rule")
public class ApprovalRuleEntity extends AbstractAuditableEntity {

    @Id
    @Column(name = "rule_id", nullable = false, length = 50)
    private String ruleId;

    @Column(name = "rule_name", nullable = false, length = 150)
    private String ruleName;

    @Column(name = "rule_type", nullable = false, length = 50)
    private String ruleType;

    @Column(name = "metric_key", nullable = false, length = 50)
    private String metricKey;

    @Column(name = "operator_key", nullable = false, length = 10)
    private String operator;

    @Column(name = "threshold_value", nullable = false, precision = 18, scale = 4)
    private BigDecimal thresholdValue;

    @Column(name = "action_routing", nullable = false, length = 50)
    private String actionRouting;

    @Column(name = "condition_expression", length = 500)
    private String conditionExpression;

    @Column(name = "base_approver", length = 100)
    private String baseApprover;

    @Column(name = "escalated_approver", length = 100)
    private String escalatedApprover;

    @Column(name = "committee_approver", length = 100)
    private String committeeApprover;

    @Column(name = "board_approver", length = 100)
    private String boardApprover;

    @Column(name = "justification_required", nullable = false)
    private boolean justificationRequired;

    @Column(name = "active_flag", nullable = false)
    private boolean active;

    @Column(name = "message_template", length = 500)
    private String messageTemplate;

    public String getRuleId() {
        return ruleId;
    }

    public void setRuleId(String ruleId) {
        this.ruleId = ruleId;
    }

    public String getRuleName() {
        return ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }

    public String getRuleType() {
        return ruleType;
    }

    public void setRuleType(String ruleType) {
        this.ruleType = ruleType;
    }

    public String getMetricKey() {
        return metricKey;
    }

    public void setMetricKey(String metricKey) {
        this.metricKey = metricKey;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public BigDecimal getThresholdValue() {
        return thresholdValue;
    }

    public void setThresholdValue(BigDecimal thresholdValue) {
        this.thresholdValue = thresholdValue;
    }

    public String getActionRouting() {
        return actionRouting;
    }

    public void setActionRouting(String actionRouting) {
        this.actionRouting = actionRouting;
    }

    public String getConditionExpression() {
        return conditionExpression;
    }

    public void setConditionExpression(String conditionExpression) {
        this.conditionExpression = conditionExpression;
    }

    public String getBaseApprover() {
        return baseApprover;
    }

    public void setBaseApprover(String baseApprover) {
        this.baseApprover = baseApprover;
    }

    public String getEscalatedApprover() {
        return escalatedApprover;
    }

    public void setEscalatedApprover(String escalatedApprover) {
        this.escalatedApprover = escalatedApprover;
    }

    public String getCommitteeApprover() {
        return committeeApprover;
    }

    public void setCommitteeApprover(String committeeApprover) {
        this.committeeApprover = committeeApprover;
    }

    public String getBoardApprover() {
        return boardApprover;
    }

    public void setBoardApprover(String boardApprover) {
        this.boardApprover = boardApprover;
    }

    public boolean isJustificationRequired() {
        return justificationRequired;
    }

    public void setJustificationRequired(boolean justificationRequired) {
        this.justificationRequired = justificationRequired;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getMessageTemplate() {
        return messageTemplate;
    }

    public void setMessageTemplate(String messageTemplate) {
        this.messageTemplate = messageTemplate;
    }

}
