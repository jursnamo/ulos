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

@Entity
@Table(name = "customer_related_party")
public class CustomerRelatedPartyEntity extends AbstractAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_cif", nullable = false)
    private CustomerPortfolioEntity customer;

    @Column(name = "relation_type", nullable = false, length = 50)
    private String relationType;

    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @Column(name = "identity_number", length = 100)
    private String identityNumber;

    @Column(name = "contact_details", length = 150)
    private String contactDetails;

    @Column(name = "address", length = 500)
    private String address;

    public Long getId() {
        return id;
    }

    public CustomerPortfolioEntity getCustomer() {
        return customer;
    }

    public void setCustomer(CustomerPortfolioEntity customer) {
        this.customer = customer;
    }

    public String getRelationType() {
        return relationType;
    }

    public void setRelationType(String relationType) {
        this.relationType = relationType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdentityNumber() {
        return identityNumber;
    }

    public void setIdentityNumber(String identityNumber) {
        this.identityNumber = identityNumber;
    }

    public String getContactDetails() {
        return contactDetails;
    }

    public void setContactDetails(String contactDetails) {
        this.contactDetails = contactDetails;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
