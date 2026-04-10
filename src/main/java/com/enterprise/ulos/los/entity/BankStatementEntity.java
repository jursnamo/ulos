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

@Entity
@Table(name = "bank_statement")
public class BankStatementEntity extends AbstractAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_cif", nullable = false)
    private CustomerPortfolioEntity customer;

    @Column(name = "bank_name", nullable = false, length = 150)
    private String bankName;

    @Column(name = "account_number", nullable = false, length = 100)
    private String accountNumber;

    @Column(name = "period_label", nullable = false, length = 20)
    private String periodLabel;

    @Column(name = "total_inflow", precision = 18, scale = 2)
    private BigDecimal totalInflow;

    @Column(name = "total_outflow", precision = 18, scale = 2)
    private BigDecimal totalOutflow;

    @Column(name = "average_balance", precision = 18, scale = 2)
    private BigDecimal averageBalance;

    @Column(name = "cheque_return_count")
    private Integer chequeReturnCount;

    @Column(name = "cheque_return_nominal", precision = 18, scale = 2)
    private BigDecimal chequeReturnNominal;

    public Long getId() {
        return id;
    }

    public CustomerPortfolioEntity getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerPortfolioEntity customer) {
        this.customer = customer;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getPeriodLabel() {
        return periodLabel;
    }

    public void setPeriodLabel(String periodLabel) {
        this.periodLabel = periodLabel;
    }

    public BigDecimal getTotalInflow() {
        return totalInflow;
    }

    public void setTotalInflow(BigDecimal totalInflow) {
        this.totalInflow = totalInflow;
    }

    public BigDecimal getTotalOutflow() {
        return totalOutflow;
    }

    public void setTotalOutflow(BigDecimal totalOutflow) {
        this.totalOutflow = totalOutflow;
    }

    public BigDecimal getAverageBalance() {
        return averageBalance;
    }

    public void setAverageBalance(BigDecimal averageBalance) {
        this.averageBalance = averageBalance;
    }

    public Integer getChequeReturnCount() {
        return chequeReturnCount;
    }

    public void setChequeReturnCount(Integer chequeReturnCount) {
        this.chequeReturnCount = chequeReturnCount;
    }

    public BigDecimal getChequeReturnNominal() {
        return chequeReturnNominal;
    }

    public void setChequeReturnNominal(BigDecimal chequeReturnNominal) {
        this.chequeReturnNominal = chequeReturnNominal;
    }
}
