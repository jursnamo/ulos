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
@Table(name = "customer_shareholder")
public class CustomerShareholderEntity extends AbstractAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_cif", nullable = false)
    private CustomerPortfolioEntity customer;

    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @Column(name = "ownership_percentage", precision = 9, scale = 2)
    private BigDecimal ownershipPercentage;

    @Column(name = "share_nominal", precision = 18, scale = 2)
    private BigDecimal shareNominal;

    public Long getId() {
        return id;
    }

    public CustomerPortfolioEntity getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerPortfolioEntity customer) {
        this.customer = customer;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getOwnershipPercentage() {
        return ownershipPercentage;
    }

    public void setOwnershipPercentage(BigDecimal ownershipPercentage) {
        this.ownershipPercentage = ownershipPercentage;
    }

    public BigDecimal getShareNominal() {
        return shareNominal;
    }

    public void setShareNominal(BigDecimal shareNominal) {
        this.shareNominal = shareNominal;
    }
}
