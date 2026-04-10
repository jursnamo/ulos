package com.enterprise.ulos.los.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "credit_application")
public class CreditApplicationEntity extends AbstractAuditableEntity {

    @Id
    @Column(name = "application_id", nullable = false, length = 50)
    private String applicationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_cif", nullable = false)
    private CustomerPortfolioEntity customer;

    @Column(name = "application_date", nullable = false)
    private LocalDate applicationDate;

    @Column(name = "application_type", nullable = false, length = 50)
    private String applicationType;

    @Column(name = "rm_username", nullable = false, length = 100)
    private String rmUsername;

    @Column(name = "branch_name", length = 100)
    private String branchName;

    @Column(name = "region_name", length = 100)
    private String regionName;

    @Column(name = "cbc_name", length = 100)
    private String cbcName;

    @Column(name = "group_relationship_status", length = 50)
    private String groupRelationshipStatus;

    @Column(name = "core_capital_bank", precision = 18, scale = 2)
    private BigDecimal coreCapitalBank;

    @Column(name = "max_lending_limit_percentage", precision = 9, scale = 2)
    private BigDecimal maxLendingLimitPercentage;

    @Column(name = "existing_exposure_group", precision = 18, scale = 2)
    private BigDecimal existingExposureGroup;

    @Column(name = "proposed_exposure", precision = 18, scale = 2)
    private BigDecimal proposedExposure;

    @Column(name = "available_limit", precision = 18, scale = 2)
    private BigDecimal availableLimit;

    @Column(name = "bi_sector_code", length = 50)
    private String biSectorCode;

    @Column(name = "sub_sector_description", length = 150)
    private String subSectorDescription;

    @Column(name = "industry_outlook", length = 50)
    private String industryOutlook;

    @Column(name = "esg_green_status", length = 100)
    private String esgGreenFinancingStatus;

    @Column(name = "justification_note", length = 2000)
    private String justificationNote;

    @Column(name = "workflow_status", nullable = false, length = 50)
    private String workflowStatus;

    @Column(name = "current_approval_tier", length = 100)
    private String currentApprovalTier;

    @Column(name = "process_instance_id", unique = true, length = 100)
    private String processInstanceId;

    @Column(name = "collateral_coverage", precision = 18, scale = 4)
    private BigDecimal collateralCoverage;

    @Column(name = "current_ratio", precision = 18, scale = 4)
    private BigDecimal currentRatio;

    @Column(name = "debt_to_equity_ratio", precision = 18, scale = 4)
    private BigDecimal debtToEquityRatio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private AppUserEntity createdBy;

    @OneToMany(mappedBy = "application", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FacilityEntity> facilities = new ArrayList<>();

    @OneToMany(mappedBy = "application", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CollateralEntity> collaterals = new ArrayList<>();

    @OneToMany(mappedBy = "application", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TboItemEntity> tboItems = new ArrayList<>();

    @OneToMany(mappedBy = "application", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CovenantItemEntity> covenants = new ArrayList<>();

    @OneToMany(mappedBy = "application", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ApplicationRuleHitEntity> ruleHits = new ArrayList<>();

    @OneToMany(mappedBy = "application", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ApplicationApprovalHistoryEntity> approvalHistories = new ArrayList<>();

    public String getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }

    public CustomerPortfolioEntity getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerPortfolioEntity customer) {
        this.customer = customer;
    }

    public LocalDate getApplicationDate() {
        return applicationDate;
    }

    public void setApplicationDate(LocalDate applicationDate) {
        this.applicationDate = applicationDate;
    }

    public String getApplicationType() {
        return applicationType;
    }

    public void setApplicationType(String applicationType) {
        this.applicationType = applicationType;
    }

    public String getRmUsername() {
        return rmUsername;
    }

    public void setRmUsername(String rmUsername) {
        this.rmUsername = rmUsername;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    public String getCbcName() {
        return cbcName;
    }

    public void setCbcName(String cbcName) {
        this.cbcName = cbcName;
    }

    public String getGroupRelationshipStatus() {
        return groupRelationshipStatus;
    }

    public void setGroupRelationshipStatus(String groupRelationshipStatus) {
        this.groupRelationshipStatus = groupRelationshipStatus;
    }

    public BigDecimal getCoreCapitalBank() {
        return coreCapitalBank;
    }

    public void setCoreCapitalBank(BigDecimal coreCapitalBank) {
        this.coreCapitalBank = coreCapitalBank;
    }

    public BigDecimal getMaxLendingLimitPercentage() {
        return maxLendingLimitPercentage;
    }

    public void setMaxLendingLimitPercentage(BigDecimal maxLendingLimitPercentage) {
        this.maxLendingLimitPercentage = maxLendingLimitPercentage;
    }

    public BigDecimal getExistingExposureGroup() {
        return existingExposureGroup;
    }

    public void setExistingExposureGroup(BigDecimal existingExposureGroup) {
        this.existingExposureGroup = existingExposureGroup;
    }

    public BigDecimal getProposedExposure() {
        return proposedExposure;
    }

    public void setProposedExposure(BigDecimal proposedExposure) {
        this.proposedExposure = proposedExposure;
    }

    public BigDecimal getAvailableLimit() {
        return availableLimit;
    }

    public void setAvailableLimit(BigDecimal availableLimit) {
        this.availableLimit = availableLimit;
    }

    public String getBiSectorCode() {
        return biSectorCode;
    }

    public void setBiSectorCode(String biSectorCode) {
        this.biSectorCode = biSectorCode;
    }

    public String getSubSectorDescription() {
        return subSectorDescription;
    }

    public void setSubSectorDescription(String subSectorDescription) {
        this.subSectorDescription = subSectorDescription;
    }

    public String getIndustryOutlook() {
        return industryOutlook;
    }

    public void setIndustryOutlook(String industryOutlook) {
        this.industryOutlook = industryOutlook;
    }

    public String getEsgGreenFinancingStatus() {
        return esgGreenFinancingStatus;
    }

    public void setEsgGreenFinancingStatus(String esgGreenFinancingStatus) {
        this.esgGreenFinancingStatus = esgGreenFinancingStatus;
    }

    public String getJustificationNote() {
        return justificationNote;
    }

    public void setJustificationNote(String justificationNote) {
        this.justificationNote = justificationNote;
    }

    public String getWorkflowStatus() {
        return workflowStatus;
    }

    public void setWorkflowStatus(String workflowStatus) {
        this.workflowStatus = workflowStatus;
    }

    public String getCurrentApprovalTier() {
        return currentApprovalTier;
    }

    public void setCurrentApprovalTier(String currentApprovalTier) {
        this.currentApprovalTier = currentApprovalTier;
    }

    public String getProcessInstanceId() {
        return processInstanceId;
    }

    public void setProcessInstanceId(String processInstanceId) {
        this.processInstanceId = processInstanceId;
    }

    public BigDecimal getCollateralCoverage() {
        return collateralCoverage;
    }

    public void setCollateralCoverage(BigDecimal collateralCoverage) {
        this.collateralCoverage = collateralCoverage;
    }

    public BigDecimal getCurrentRatio() {
        return currentRatio;
    }

    public void setCurrentRatio(BigDecimal currentRatio) {
        this.currentRatio = currentRatio;
    }

    public BigDecimal getDebtToEquityRatio() {
        return debtToEquityRatio;
    }

    public void setDebtToEquityRatio(BigDecimal debtToEquityRatio) {
        this.debtToEquityRatio = debtToEquityRatio;
    }

    public AppUserEntity getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(AppUserEntity createdBy) {
        this.createdBy = createdBy;
    }

    public List<FacilityEntity> getFacilities() {
        return facilities;
    }

    public List<CollateralEntity> getCollaterals() {
        return collaterals;
    }

    public List<TboItemEntity> getTboItems() {
        return tboItems;
    }

    public List<CovenantItemEntity> getCovenants() {
        return covenants;
    }

    public List<ApplicationRuleHitEntity> getRuleHits() {
        return ruleHits;
    }

    public List<ApplicationApprovalHistoryEntity> getApprovalHistories() {
        return approvalHistories;
    }
}
