package com.enterprise.ulos.los.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "financial_statement")
public class FinancialStatementEntity extends AbstractAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_cif", nullable = false)
    private CustomerPortfolioEntity customer;

    @Column(name = "statement_id", length = 100)
    private String statementId;

    @Column(name = "period_label", nullable = false, length = 20)
    private String periodLabel;

    @Column(name = "audit_status", length = 50)
    private String auditStatus;

    @Column(name = "auditor_name", length = 150)
    private String auditorName;

    @Column(name = "group_holding_name", length = 150)
    private String groupHoldingName;

    @Column(name = "consolidated_flag", nullable = false)
    private boolean consolidated;

    @Column(name = "cash", precision = 18, scale = 2)
    private BigDecimal cash;

    @Column(name = "accounts_receivable", precision = 18, scale = 2)
    private BigDecimal accountsReceivable;

    @Column(name = "inventory", precision = 18, scale = 2)
    private BigDecimal inventory;

    @Column(name = "fixed_assets", precision = 18, scale = 2)
    private BigDecimal fixedAssets;

    @Column(name = "accounts_payable", precision = 18, scale = 2)
    private BigDecimal accountsPayable;

    @Column(name = "short_term_loan", precision = 18, scale = 2)
    private BigDecimal shortTermLoan;

    @Column(name = "long_term_loan", precision = 18, scale = 2)
    private BigDecimal longTermLoan;

    @Column(name = "total_equity", precision = 18, scale = 2)
    private BigDecimal totalEquity;

    @Column(name = "sales_revenue", precision = 18, scale = 2)
    private BigDecimal salesRevenue;

    @Column(name = "cogs", precision = 18, scale = 2)
    private BigDecimal cogs;

    @Column(name = "gross_profit", precision = 18, scale = 2)
    private BigDecimal grossProfit;

    @Column(name = "operating_expenses", precision = 18, scale = 2)
    private BigDecimal operatingExpenses;

    @Column(name = "ebitda", precision = 18, scale = 2)
    private BigDecimal ebitda;

    @Column(name = "interest_expense", precision = 18, scale = 2)
    private BigDecimal interestExpense;

    @Column(name = "net_income", precision = 18, scale = 2)
    private BigDecimal netIncome;

    @Column(name = "intercompany_elimination", precision = 18, scale = 2)
    private BigDecimal intercompanyElimination;

    @OneToOne(mappedBy = "financialStatement", cascade = CascadeType.ALL, orphanRemoval = true)
    private FinancialAnalysisSnapshotEntity analysisSnapshot;

    public Long getId() {
        return id;
    }

    public CustomerPortfolioEntity getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerPortfolioEntity customer) {
        this.customer = customer;
    }

    public String getStatementId() {
        return statementId;
    }

    public void setStatementId(String statementId) {
        this.statementId = statementId;
    }

    public String getPeriodLabel() {
        return periodLabel;
    }

    public void setPeriodLabel(String periodLabel) {
        this.periodLabel = periodLabel;
    }

    public String getAuditStatus() {
        return auditStatus;
    }

    public void setAuditStatus(String auditStatus) {
        this.auditStatus = auditStatus;
    }

    public String getAuditorName() {
        return auditorName;
    }

    public void setAuditorName(String auditorName) {
        this.auditorName = auditorName;
    }

    public String getGroupHoldingName() {
        return groupHoldingName;
    }

    public void setGroupHoldingName(String groupHoldingName) {
        this.groupHoldingName = groupHoldingName;
    }

    public boolean isConsolidated() {
        return consolidated;
    }

    public void setConsolidated(boolean consolidated) {
        this.consolidated = consolidated;
    }

    public BigDecimal getCash() {
        return cash;
    }

    public void setCash(BigDecimal cash) {
        this.cash = cash;
    }

    public BigDecimal getAccountsReceivable() {
        return accountsReceivable;
    }

    public void setAccountsReceivable(BigDecimal accountsReceivable) {
        this.accountsReceivable = accountsReceivable;
    }

    public BigDecimal getInventory() {
        return inventory;
    }

    public void setInventory(BigDecimal inventory) {
        this.inventory = inventory;
    }

    public BigDecimal getFixedAssets() {
        return fixedAssets;
    }

    public void setFixedAssets(BigDecimal fixedAssets) {
        this.fixedAssets = fixedAssets;
    }

    public BigDecimal getAccountsPayable() {
        return accountsPayable;
    }

    public void setAccountsPayable(BigDecimal accountsPayable) {
        this.accountsPayable = accountsPayable;
    }

    public BigDecimal getShortTermLoan() {
        return shortTermLoan;
    }

    public void setShortTermLoan(BigDecimal shortTermLoan) {
        this.shortTermLoan = shortTermLoan;
    }

    public BigDecimal getLongTermLoan() {
        return longTermLoan;
    }

    public void setLongTermLoan(BigDecimal longTermLoan) {
        this.longTermLoan = longTermLoan;
    }

    public BigDecimal getTotalEquity() {
        return totalEquity;
    }

    public void setTotalEquity(BigDecimal totalEquity) {
        this.totalEquity = totalEquity;
    }

    public BigDecimal getSalesRevenue() {
        return salesRevenue;
    }

    public void setSalesRevenue(BigDecimal salesRevenue) {
        this.salesRevenue = salesRevenue;
    }

    public BigDecimal getCogs() {
        return cogs;
    }

    public void setCogs(BigDecimal cogs) {
        this.cogs = cogs;
    }

    public BigDecimal getGrossProfit() {
        return grossProfit;
    }

    public void setGrossProfit(BigDecimal grossProfit) {
        this.grossProfit = grossProfit;
    }

    public BigDecimal getOperatingExpenses() {
        return operatingExpenses;
    }

    public void setOperatingExpenses(BigDecimal operatingExpenses) {
        this.operatingExpenses = operatingExpenses;
    }

    public BigDecimal getEbitda() {
        return ebitda;
    }

    public void setEbitda(BigDecimal ebitda) {
        this.ebitda = ebitda;
    }

    public BigDecimal getInterestExpense() {
        return interestExpense;
    }

    public void setInterestExpense(BigDecimal interestExpense) {
        this.interestExpense = interestExpense;
    }

    public BigDecimal getNetIncome() {
        return netIncome;
    }

    public void setNetIncome(BigDecimal netIncome) {
        this.netIncome = netIncome;
    }

    public BigDecimal getIntercompanyElimination() {
        return intercompanyElimination;
    }

    public void setIntercompanyElimination(BigDecimal intercompanyElimination) {
        this.intercompanyElimination = intercompanyElimination;
    }

    public FinancialAnalysisSnapshotEntity getAnalysisSnapshot() {
        return analysisSnapshot;
    }

    public void setAnalysisSnapshot(FinancialAnalysisSnapshotEntity analysisSnapshot) {
        this.analysisSnapshot = analysisSnapshot;
    }
}
