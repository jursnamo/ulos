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
import java.time.LocalDate;

@Entity
@Table(name = "facility")
public class FacilityEntity extends AbstractAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", nullable = false)
    private CreditApplicationEntity application;

    @Column(name = "facility_code", nullable = false, length = 50)
    private String facilityCode;

    @Column(name = "facility_name", nullable = false, length = 100)
    private String facilityName;

    @Column(name = "revolving_status", length = 50)
    private String revolvingStatus;

    @Column(name = "currency_code", length = 10)
    private String currencyCode;

    @Column(name = "limit_amount", precision = 18, scale = 2)
    private BigDecimal limitAmount;

    @Column(name = "tenor_months")
    private Integer tenorMonths;

    @Column(name = "maturity_date")
    private LocalDate maturityDate;

    @Column(name = "interest_rate_type", length = 50)
    private String interestRateType;

    @Column(name = "interest_rate", precision = 9, scale = 4)
    private BigDecimal interestRate;

    @Column(name = "provision_fee", precision = 9, scale = 4)
    private BigDecimal provisionFee;

    @Column(name = "admin_fee", precision = 18, scale = 2)
    private BigDecimal adminFee;

    @Column(name = "commitment_fee", precision = 9, scale = 4)
    private BigDecimal commitmentFee;

    @Column(name = "penalty_fee", precision = 9, scale = 4)
    private BigDecimal penaltyFee;

    @Column(name = "repayment_type", length = 100)
    private String repaymentType;

    @Column(name = "purpose_of_loan", length = 500)
    private String purposeOfLoan;

    public Long getId() {
        return id;
    }

    public CreditApplicationEntity getApplication() {
        return application;
    }

    public void setApplication(CreditApplicationEntity application) {
        this.application = application;
    }

    public String getFacilityCode() {
        return facilityCode;
    }

    public void setFacilityCode(String facilityCode) {
        this.facilityCode = facilityCode;
    }

    public String getFacilityName() {
        return facilityName;
    }

    public void setFacilityName(String facilityName) {
        this.facilityName = facilityName;
    }

    public String getRevolvingStatus() {
        return revolvingStatus;
    }

    public void setRevolvingStatus(String revolvingStatus) {
        this.revolvingStatus = revolvingStatus;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public BigDecimal getLimitAmount() {
        return limitAmount;
    }

    public void setLimitAmount(BigDecimal limitAmount) {
        this.limitAmount = limitAmount;
    }

    public Integer getTenorMonths() {
        return tenorMonths;
    }

    public void setTenorMonths(Integer tenorMonths) {
        this.tenorMonths = tenorMonths;
    }

    public LocalDate getMaturityDate() {
        return maturityDate;
    }

    public void setMaturityDate(LocalDate maturityDate) {
        this.maturityDate = maturityDate;
    }

    public String getInterestRateType() {
        return interestRateType;
    }

    public void setInterestRateType(String interestRateType) {
        this.interestRateType = interestRateType;
    }

    public BigDecimal getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(BigDecimal interestRate) {
        this.interestRate = interestRate;
    }

    public BigDecimal getProvisionFee() {
        return provisionFee;
    }

    public void setProvisionFee(BigDecimal provisionFee) {
        this.provisionFee = provisionFee;
    }

    public BigDecimal getAdminFee() {
        return adminFee;
    }

    public void setAdminFee(BigDecimal adminFee) {
        this.adminFee = adminFee;
    }

    public BigDecimal getCommitmentFee() {
        return commitmentFee;
    }

    public void setCommitmentFee(BigDecimal commitmentFee) {
        this.commitmentFee = commitmentFee;
    }

    public BigDecimal getPenaltyFee() {
        return penaltyFee;
    }

    public void setPenaltyFee(BigDecimal penaltyFee) {
        this.penaltyFee = penaltyFee;
    }

    public String getRepaymentType() {
        return repaymentType;
    }

    public void setRepaymentType(String repaymentType) {
        this.repaymentType = repaymentType;
    }

    public String getPurposeOfLoan() {
        return purposeOfLoan;
    }

    public void setPurposeOfLoan(String purposeOfLoan) {
        this.purposeOfLoan = purposeOfLoan;
    }
}
