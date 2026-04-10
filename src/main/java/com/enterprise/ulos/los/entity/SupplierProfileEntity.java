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
@Table(name = "supplier_profile")
public class SupplierProfileEntity extends AbstractAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_cif", nullable = false)
    private CustomerPortfolioEntity customer;

    @Column(name = "supplier_name", nullable = false, length = 150)
    private String supplierName;

    @Column(name = "purchase_percentage", precision = 9, scale = 2)
    private BigDecimal purchasePercentage;

    @Column(name = "payment_terms_days")
    private Integer paymentTermsDays;

    public Long getId() {
        return id;
    }

    public CustomerPortfolioEntity getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerPortfolioEntity customer) {
        this.customer = customer;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public BigDecimal getPurchasePercentage() {
        return purchasePercentage;
    }

    public void setPurchasePercentage(BigDecimal purchasePercentage) {
        this.purchasePercentage = purchasePercentage;
    }

    public Integer getPaymentTermsDays() {
        return paymentTermsDays;
    }

    public void setPaymentTermsDays(Integer paymentTermsDays) {
        this.paymentTermsDays = paymentTermsDays;
    }
}
