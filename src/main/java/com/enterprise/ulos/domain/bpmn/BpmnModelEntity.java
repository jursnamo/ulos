package com.enterprise.ulos.domain.bpmn;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import com.enterprise.ulos.los.entity.AbstractAuditableEntity;

@Entity
@Table(name = "bpmn_model")
public class BpmnModelEntity extends AbstractAuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "process_key", nullable = false, length = 100)
    private String processKey;

    @Column(name = "process_name", nullable = false, length = 200)
    private String processName;

    @Column(name = "resource_name", nullable = false, length = 200)
    private String resourceName;

    @Lob
    @Column(name = "bpmn_xml", nullable = false, columnDefinition = "LONGTEXT")
    private String bpmnXml;

    @Column(name = "deployment_id", length = 100)
    private String deploymentId;

    @Column(name = "process_definition_id", length = 150)
    private String processDefinitionId;

    @Column(name = "version_no", nullable = false)
    private Integer version;

    @Column(name = "active_flag", nullable = false)
    private boolean active;

    @Column(name = "deployed_by", length = 100)
    private String deployedBy;

    @Column(name = "change_summary", length = 500)
    private String changeSummary;

    public Long getId() {
        return id;
    }

    public String getProcessKey() {
        return processKey;
    }

    public void setProcessKey(String processKey) {
        this.processKey = processKey;
    }

    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public String getBpmnXml() {
        return bpmnXml;
    }

    public void setBpmnXml(String bpmnXml) {
        this.bpmnXml = bpmnXml;
    }

    public String getDeploymentId() {
        return deploymentId;
    }

    public void setDeploymentId(String deploymentId) {
        this.deploymentId = deploymentId;
    }

    public String getProcessDefinitionId() {
        return processDefinitionId;
    }

    public void setProcessDefinitionId(String processDefinitionId) {
        this.processDefinitionId = processDefinitionId;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getDeployedBy() {
        return deployedBy;
    }

    public void setDeployedBy(String deployedBy) {
        this.deployedBy = deployedBy;
    }

    public String getChangeSummary() {
        return changeSummary;
    }

    public void setChangeSummary(String changeSummary) {
        this.changeSummary = changeSummary;
    }
}
