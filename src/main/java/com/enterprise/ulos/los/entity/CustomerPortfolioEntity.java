package com.enterprise.ulos.los.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "customer_portfolio")
public class CustomerPortfolioEntity extends AbstractAuditableEntity {

    @Id
    @Column(name = "cif_number", nullable = false, length = 50)
    private String cifNumber;

    @Column(name = "company_name", nullable = false, length = 200)
    private String companyName;

    @Column(name = "company_type", nullable = false, length = 50)
    private String companyType;

    @Column(name = "date_of_establishment")
    private LocalDate dateOfEstablishment;

    @Column(name = "place_of_establishment", length = 150)
    private String placeOfEstablishment;

    @Column(name = "tax_id", length = 50)
    private String taxId;

    @Column(name = "business_license", length = 100)
    private String businessLicense;

    @Column(name = "office_address", length = 500)
    private String officeAddress;

    @Column(name = "factory_address", length = 500)
    private String factoryAddress;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CustomerKeyManagementEntity> keyManagementItems = new ArrayList<>();

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CustomerShareholderEntity> shareholders = new ArrayList<>();

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CustomerRelatedPartyEntity> relatedParties = new ArrayList<>();

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FinancialStatementEntity> financialStatements = new ArrayList<>();

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BankStatementEntity> bankStatements = new ArrayList<>();

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SupplierProfileEntity> suppliers = new ArrayList<>();

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BuyerProfileEntity> buyers = new ArrayList<>();

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CompetitorProfileEntity> competitors = new ArrayList<>();

    public String getCifNumber() {
        return cifNumber;
    }

    public void setCifNumber(String cifNumber) {
        this.cifNumber = cifNumber;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyType() {
        return companyType;
    }

    public void setCompanyType(String companyType) {
        this.companyType = companyType;
    }

    public LocalDate getDateOfEstablishment() {
        return dateOfEstablishment;
    }

    public void setDateOfEstablishment(LocalDate dateOfEstablishment) {
        this.dateOfEstablishment = dateOfEstablishment;
    }

    public String getPlaceOfEstablishment() {
        return placeOfEstablishment;
    }

    public void setPlaceOfEstablishment(String placeOfEstablishment) {
        this.placeOfEstablishment = placeOfEstablishment;
    }

    public String getTaxId() {
        return taxId;
    }

    public void setTaxId(String taxId) {
        this.taxId = taxId;
    }

    public String getBusinessLicense() {
        return businessLicense;
    }

    public void setBusinessLicense(String businessLicense) {
        this.businessLicense = businessLicense;
    }

    public String getOfficeAddress() {
        return officeAddress;
    }

    public void setOfficeAddress(String officeAddress) {
        this.officeAddress = officeAddress;
    }

    public String getFactoryAddress() {
        return factoryAddress;
    }

    public void setFactoryAddress(String factoryAddress) {
        this.factoryAddress = factoryAddress;
    }

    public List<CustomerKeyManagementEntity> getKeyManagementItems() {
        return keyManagementItems;
    }

    public List<CustomerShareholderEntity> getShareholders() {
        return shareholders;
    }

    public List<CustomerRelatedPartyEntity> getRelatedParties() {
        return relatedParties;
    }

    public List<FinancialStatementEntity> getFinancialStatements() {
        return financialStatements;
    }

    public List<BankStatementEntity> getBankStatements() {
        return bankStatements;
    }

    public List<SupplierProfileEntity> getSuppliers() {
        return suppliers;
    }

    public List<BuyerProfileEntity> getBuyers() {
        return buyers;
    }

    public List<CompetitorProfileEntity> getCompetitors() {
        return competitors;
    }
}
