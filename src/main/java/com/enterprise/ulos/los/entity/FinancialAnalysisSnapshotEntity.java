package com.enterprise.ulos.los.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "financial_analysis_snapshot")
public class FinancialAnalysisSnapshotEntity extends AbstractAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "financial_statement_id", nullable = false, unique = true)
    private FinancialStatementEntity financialStatement;

    @Column(name = "current_ratio", precision = 18, scale = 4)
    private BigDecimal currentRatio;

    @Column(name = "quick_ratio", precision = 18, scale = 4)
    private BigDecimal quickRatio;

    @Column(name = "debt_to_equity_ratio", precision = 18, scale = 4)
    private BigDecimal debtToEquityRatio;

    @Column(name = "interest_coverage_ratio", precision = 18, scale = 4)
    private BigDecimal interestCoverageRatio;

    @Column(name = "return_on_assets", precision = 18, scale = 4)
    private BigDecimal returnOnAssets;

    @Column(name = "return_on_equity", precision = 18, scale = 4)
    private BigDecimal returnOnEquity;

    @Column(name = "gross_profit_margin", precision = 18, scale = 4)
    private BigDecimal grossProfitMargin;

    @Column(name = "net_profit_margin", precision = 18, scale = 4)
    private BigDecimal netProfitMargin;

    @Column(name = "ar_days", precision = 18, scale = 4)
    private BigDecimal arDays;

    @Column(name = "inventory_days", precision = 18, scale = 4)
    private BigDecimal inventoryDays;

    @Column(name = "ap_days", precision = 18, scale = 4)
    private BigDecimal apDays;

    @Column(name = "cash_conversion_cycle", precision = 18, scale = 4)
    private BigDecimal cashConversionCycle;

    public Long getId() {
        return id;
    }

    public FinancialStatementEntity getFinancialStatement() {
        return financialStatement;
    }

    public void setFinancialStatement(FinancialStatementEntity financialStatement) {
        this.financialStatement = financialStatement;
    }

    public BigDecimal getCurrentRatio() {
        return currentRatio;
    }

    public void setCurrentRatio(BigDecimal currentRatio) {
        this.currentRatio = currentRatio;
    }

    public BigDecimal getQuickRatio() {
        return quickRatio;
    }

    public void setQuickRatio(BigDecimal quickRatio) {
        this.quickRatio = quickRatio;
    }

    public BigDecimal getDebtToEquityRatio() {
        return debtToEquityRatio;
    }

    public void setDebtToEquityRatio(BigDecimal debtToEquityRatio) {
        this.debtToEquityRatio = debtToEquityRatio;
    }

    public BigDecimal getInterestCoverageRatio() {
        return interestCoverageRatio;
    }

    public void setInterestCoverageRatio(BigDecimal interestCoverageRatio) {
        this.interestCoverageRatio = interestCoverageRatio;
    }

    public BigDecimal getReturnOnAssets() {
        return returnOnAssets;
    }

    public void setReturnOnAssets(BigDecimal returnOnAssets) {
        this.returnOnAssets = returnOnAssets;
    }

    public BigDecimal getReturnOnEquity() {
        return returnOnEquity;
    }

    public void setReturnOnEquity(BigDecimal returnOnEquity) {
        this.returnOnEquity = returnOnEquity;
    }

    public BigDecimal getGrossProfitMargin() {
        return grossProfitMargin;
    }

    public void setGrossProfitMargin(BigDecimal grossProfitMargin) {
        this.grossProfitMargin = grossProfitMargin;
    }

    public BigDecimal getNetProfitMargin() {
        return netProfitMargin;
    }

    public void setNetProfitMargin(BigDecimal netProfitMargin) {
        this.netProfitMargin = netProfitMargin;
    }

    public BigDecimal getArDays() {
        return arDays;
    }

    public void setArDays(BigDecimal arDays) {
        this.arDays = arDays;
    }

    public BigDecimal getInventoryDays() {
        return inventoryDays;
    }

    public void setInventoryDays(BigDecimal inventoryDays) {
        this.inventoryDays = inventoryDays;
    }

    public BigDecimal getApDays() {
        return apDays;
    }

    public void setApDays(BigDecimal apDays) {
        this.apDays = apDays;
    }

    public BigDecimal getCashConversionCycle() {
        return cashConversionCycle;
    }

    public void setCashConversionCycle(BigDecimal cashConversionCycle) {
        this.cashConversionCycle = cashConversionCycle;
    }
}
