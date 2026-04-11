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

import java.time.LocalDateTime;

@Entity
@Table(name = "los_workflow_history")
public class WorkflowHistoryEntity extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", nullable = false)
    private ApplicationWorkspaceEntity application;

    @Column(name = "stage_code", nullable = false, length = 80)
    private String stageCode;

    @Column(name = "decision_code", nullable = false, length = 40)
    private String decisionCode;

    @Column(name = "actor", nullable = false, length = 120)
    private String actor;

    @Column(name = "notes", length = 1000)
    private String notes;

    @Column(name = "decided_at", nullable = false)
    private LocalDateTime decidedAt;

    public Long getId() {
        return id;
    }

    public ApplicationWorkspaceEntity getApplication() {
        return application;
    }

    public void setApplication(ApplicationWorkspaceEntity application) {
        this.application = application;
    }

    public String getStageCode() {
        return stageCode;
    }

    public void setStageCode(String stageCode) {
        this.stageCode = stageCode;
    }

    public String getDecisionCode() {
        return decisionCode;
    }

    public void setDecisionCode(String decisionCode) {
        this.decisionCode = decisionCode;
    }

    public String getActor() {
        return actor;
    }

    public void setActor(String actor) {
        this.actor = actor;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getDecidedAt() {
        return decidedAt;
    }

    public void setDecidedAt(LocalDateTime decidedAt) {
        this.decidedAt = decidedAt;
    }
}
