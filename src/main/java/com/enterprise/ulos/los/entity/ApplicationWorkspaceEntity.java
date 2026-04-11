package com.enterprise.ulos.los.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "los_application")
public class ApplicationWorkspaceEntity extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "application_id", nullable = false, unique = true, length = 60)
    private String applicationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private CustomerEntity customer;

    @Column(name = "application_type", nullable = false, length = 60)
    private String applicationType;

    @Column(name = "segment_code", length = 20)
    private String segmentCode;

    @Column(name = "workflow_status", nullable = false, length = 40)
    private String workflowStatus;

    @Column(name = "current_stage", length = 80)
    private String currentStage;

    @Column(name = "process_instance_id", unique = true, length = 100)
    private String processInstanceId;

    @Column(name = "proposed_exposure", precision = 18, scale = 2)
    private BigDecimal proposedExposure;

    @Column(name = "setup_json", columnDefinition = "LONGTEXT")
    private String setupJson;

    @Column(name = "compliance_json", columnDefinition = "LONGTEXT")
    private String complianceJson;

    @Column(name = "facilities_json", columnDefinition = "LONGTEXT")
    private String facilitiesJson;

    @Column(name = "collaterals_json", columnDefinition = "LONGTEXT")
    private String collateralsJson;

    @Column(name = "links_json", columnDefinition = "LONGTEXT")
    private String linksJson;

    @Column(name = "drawdown_conditions_json", columnDefinition = "LONGTEXT")
    private String drawdownConditionsJson;

    @Column(name = "global_tc_json", columnDefinition = "LONGTEXT")
    private String globalTcJson;

    @Column(name = "tbo_json", columnDefinition = "LONGTEXT")
    private String tboJson;

    @Column(name = "tbo_docs_json", columnDefinition = "LONGTEXT")
    private String tboDocsJson;

    @Column(name = "financials_json", columnDefinition = "LONGTEXT")
    private String financialsJson;

    @Column(name = "slik_json", columnDefinition = "LONGTEXT")
    private String slikJson;

    @Column(name = "remarks", columnDefinition = "LONGTEXT")
    private String remarks;

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    public Long getId() {
        return id;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public CustomerEntity getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerEntity customer) {
        this.customer = customer;
    }

    public String getApplicationType() {
        return applicationType;
    }

    public void setApplicationType(String applicationType) {
        this.applicationType = applicationType;
    }

    public String getSegmentCode() {
        return segmentCode;
    }

    public void setSegmentCode(String segmentCode) {
        this.segmentCode = segmentCode;
    }

    public String getWorkflowStatus() {
        return workflowStatus;
    }

    public void setWorkflowStatus(String workflowStatus) {
        this.workflowStatus = workflowStatus;
    }

    public String getCurrentStage() {
        return currentStage;
    }

    public void setCurrentStage(String currentStage) {
        this.currentStage = currentStage;
    }

    public String getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(String processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public BigDecimal getProposedExposure() {
        return proposedExposure;
    }

    public void setProposedExposure(BigDecimal proposedExposure) {
        this.proposedExposure = proposedExposure;
    }

    public String getSetupJson() {
        return setupJson;
    }

    public void setSetupJson(String setupJson) {
        this.setupJson = setupJson;
    }

    public String getComplianceJson() {
        return complianceJson;
    }

    public void setComplianceJson(String complianceJson) {
        this.complianceJson = complianceJson;
    }

    public String getFacilitiesJson() {
        return facilitiesJson;
    }

    public void setFacilitiesJson(String facilitiesJson) {
        this.facilitiesJson = facilitiesJson;
    }

    public String getCollateralsJson() {
        return collateralsJson;
    }

    public void setCollateralsJson(String collateralsJson) {
        this.collateralsJson = collateralsJson;
    }

    public String getLinksJson() {
        return linksJson;
    }

    public void setLinksJson(String linksJson) {
        this.linksJson = linksJson;
    }

    public String getDrawdownConditionsJson() {
        return drawdownConditionsJson;
    }

    public void setDrawdownConditionsJson(String drawdownConditionsJson) {
        this.drawdownConditionsJson = drawdownConditionsJson;
    }

    public String getGlobalTcJson() {
        return globalTcJson;
    }

    public void setGlobalTcJson(String globalTcJson) {
        this.globalTcJson = globalTcJson;
    }

    public String getTboJson() {
        return tboJson;
    }

    public void setTboJson(String tboJson) {
        this.tboJson = tboJson;
    }

    public String getTboDocsJson() {
        return tboDocsJson;
    }

    public void setTboDocsJson(String tboDocsJson) {
        this.tboDocsJson = tboDocsJson;
    }

    public String getFinancialsJson() {
        return financialsJson;
    }

    public void setFinancialsJson(String financialsJson) {
        this.financialsJson = financialsJson;
    }

    public String getSlikJson() {
        return slikJson;
    }

    public void setSlikJson(String slikJson) {
        this.slikJson = slikJson;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }
}
