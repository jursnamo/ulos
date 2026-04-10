package com.enterprise.ulos.los.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "collateral")
public class CollateralEntity extends AbstractAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", nullable = false)
    private CreditApplicationEntity application;

    @Column(name = "collateral_id", nullable = false, length = 50)
    private String collateralId;

    @Column(name = "collateral_type", nullable = false, length = 100)
    private String collateralType;

    @Column(name = "owner_name", length = 150)
    private String ownerName;

    @Column(name = "location_address", length = 500)
    private String locationAddress;

    @Column(name = "appraisal_date")
    private LocalDate appraisalDate;

    @Column(name = "appraiser_name", length = 150)
    private String appraiserName;

    @Column(name = "market_value", precision = 18, scale = 2)
    private BigDecimal marketValue;

    @Column(name = "liquidation_value", precision = 18, scale = 2)
    private BigDecimal liquidationValue;

    @Column(name = "margin_of_advance_percentage", precision = 9, scale = 2)
    private BigDecimal marginOfAdvancePercentage;

    @Column(name = "bankable_value", precision = 18, scale = 2)
    private BigDecimal bankableValue;

    @Column(name = "legal_document_info", length = 255)
    private String legalDocumentInfo;

    @Column(name = "legal_document_expiry_date")
    private LocalDate legalDocumentExpiryDate;

    @Column(name = "insurance_name", length = 150)
    private String insuranceName;

    @Column(name = "insurance_coverage_value", precision = 18, scale = 2)
    private BigDecimal insuranceCoverageValue;

    @Column(name = "insurance_expiry_date")
    private LocalDate insuranceExpiryDate;

    @ElementCollection
    @CollectionTable(name = "collateral_facility_link", joinColumns = @JoinColumn(name = "collateral_id"))
    @Column(name = "facility_code", nullable = false, length = 50)
    private Set<String> linkedFacilityCodes = new LinkedHashSet<>();

    public Long getId() {
        return id;
    }

    public CreditApplicationEntity getApplication() {
        return application;
    }

    public void setApplication(CreditApplicationEntity application) {
        this.application = application;
    }

    public String getCollateralId() {
        return collateralId;
    }

    public void setCollateralId(String collateralId) {
        this.collateralId = collateralId;
    }

    public String getCollateralType() {
        return collateralType;
    }

    public void setCollateralType(String collateralType) {
        this.collateralType = collateralType;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getLocationAddress() {
        return locationAddress;
    }

    public void setLocationAddress(String locationAddress) {
        this.locationAddress = locationAddress;
    }

    public LocalDate getAppraisalDate() {
        return appraisalDate;
    }

    public void setAppraisalDate(LocalDate appraisalDate) {
        this.appraisalDate = appraisalDate;
    }

    public String getAppraiserName() {
        return appraiserName;
    }

    public void setAppraiserName(String appraiserName) {
        this.appraiserName = appraiserName;
    }

    public BigDecimal getMarketValue() {
        return marketValue;
    }

    public void setMarketValue(BigDecimal marketValue) {
        this.marketValue = marketValue;
    }

    public BigDecimal getLiquidationValue() {
        return liquidationValue;
    }

    public void setLiquidationValue(BigDecimal liquidationValue) {
        this.liquidationValue = liquidationValue;
    }

    public BigDecimal getMarginOfAdvancePercentage() {
        return marginOfAdvancePercentage;
    }

    public void setMarginOfAdvancePercentage(BigDecimal marginOfAdvancePercentage) {
        this.marginOfAdvancePercentage = marginOfAdvancePercentage;
    }

    public BigDecimal getBankableValue() {
        return bankableValue;
    }

    public void setBankableValue(BigDecimal bankableValue) {
        this.bankableValue = bankableValue;
    }

    public String getLegalDocumentInfo() {
        return legalDocumentInfo;
    }

    public void setLegalDocumentInfo(String legalDocumentInfo) {
        this.legalDocumentInfo = legalDocumentInfo;
    }

    public LocalDate getLegalDocumentExpiryDate() {
        return legalDocumentExpiryDate;
    }

    public void setLegalDocumentExpiryDate(LocalDate legalDocumentExpiryDate) {
        this.legalDocumentExpiryDate = legalDocumentExpiryDate;
    }

    public String getInsuranceName() {
        return insuranceName;
    }

    public void setInsuranceName(String insuranceName) {
        this.insuranceName = insuranceName;
    }

    public BigDecimal getInsuranceCoverageValue() {
        return insuranceCoverageValue;
    }

    public void setInsuranceCoverageValue(BigDecimal insuranceCoverageValue) {
        this.insuranceCoverageValue = insuranceCoverageValue;
    }

    public LocalDate getInsuranceExpiryDate() {
        return insuranceExpiryDate;
    }

    public void setInsuranceExpiryDate(LocalDate insuranceExpiryDate) {
        this.insuranceExpiryDate = insuranceExpiryDate;
    }

    public Set<String> getLinkedFacilityCodes() {
        return linkedFacilityCodes;
    }
}
