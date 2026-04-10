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
@Table(name = "covenant_item")
public class CovenantItemEntity extends AbstractAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", nullable = false)
    private CreditApplicationEntity application;

    @Column(name = "covenant_id", nullable = false, length = 50)
    private String covenantId;

    @Column(name = "covenant_type", length = 100)
    private String covenantType;

    @Column(name = "description_text", length = 1000)
    private String descriptionText;

    @Column(name = "testing_frequency", length = 50)
    private String testingFrequency;

    @Column(name = "measurement_date")
    private LocalDate measurementDate;

    @Column(name = "status", length = 50)
    private String status;

    @Column(name = "penalty_details", length = 500)
    private String penaltyDetails;

    public Long getId() {
        return id;
    }

    public CreditApplicationEntity getApplication() {
        return application;
    }

    public void setApplication(CreditApplicationEntity application) {
        this.application = application;
    }

    public String getCovenantId() {
        return covenantId;
    }

    public void setCovenantId(String covenantId) {
        this.covenantId = covenantId;
    }

    public String getCovenantType() {
        return covenantType;
    }

    public void setCovenantType(String covenantType) {
        this.covenantType = covenantType;
    }

    public String getDescriptionText() {
        return descriptionText;
    }

    public void setDescriptionText(String descriptionText) {
        this.descriptionText = descriptionText;
    }

    public String getTestingFrequency() {
        return testingFrequency;
    }

    public void setTestingFrequency(String testingFrequency) {
        this.testingFrequency = testingFrequency;
    }

    public LocalDate getMeasurementDate() {
        return measurementDate;
    }

    public void setMeasurementDate(LocalDate measurementDate) {
        this.measurementDate = measurementDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPenaltyDetails() {
        return penaltyDetails;
    }

    public void setPenaltyDetails(String penaltyDetails) {
        this.penaltyDetails = penaltyDetails;
    }
}
