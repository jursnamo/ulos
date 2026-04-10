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

import java.time.LocalDate;

@Entity
@Table(name = "tbo_item")
public class TboItemEntity extends AbstractAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", nullable = false)
    private CreditApplicationEntity application;

    @Column(name = "tbo_id", nullable = false, length = 50)
    private String tboId;

    @Column(name = "linked_facility_code", length = 50)
    private String linkedFacilityCode;

    @Column(name = "tbo_category", length = 100)
    private String tboCategory;

    @Column(name = "document_requirement", length = 500)
    private String documentRequirement;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "status", length = 50)
    private String status;

    @Column(name = "pic", length = 100)
    private String pic;

    public Long getId() {
        return id;
    }

    public CreditApplicationEntity getApplication() {
        return application;
    }

    public void setApplication(CreditApplicationEntity application) {
        this.application = application;
    }

    public String getTboId() {
        return tboId;
    }

    public void setTboId(String tboId) {
        this.tboId = tboId;
    }

    public String getLinkedFacilityCode() {
        return linkedFacilityCode;
    }

    public void setLinkedFacilityCode(String linkedFacilityCode) {
        this.linkedFacilityCode = linkedFacilityCode;
    }

    public String getTboCategory() {
        return tboCategory;
    }

    public void setTboCategory(String tboCategory) {
        this.tboCategory = tboCategory;
    }

    public String getDocumentRequirement() {
        return documentRequirement;
    }

    public void setDocumentRequirement(String documentRequirement) {
        this.documentRequirement = documentRequirement;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }
}
