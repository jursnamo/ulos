package com.enterprise.ulos.los.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "los_master_facility")
public class MasterFacilityEntity extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "facility_code", nullable = false, unique = true, length = 30)
    private String facilityCode;

    @Column(name = "facility_name", nullable = false, length = 150)
    private String facilityName;

    @Column(name = "product_code", nullable = false, length = 20)
    private String productCode;

    @Column(name = "segment_codes", nullable = false, length = 120)
    private String segmentCodes;

    @Column(name = "revolving_flag", nullable = false)
    private boolean revolving;

    @Column(name = "funded_flag", nullable = false)
    private boolean funded;

    @Column(name = "non_funded_flag", nullable = false)
    private boolean nonFunded;

    @Column(name = "active_flag", nullable = false)
    private boolean active;

    @Column(name = "field_mapping_json", columnDefinition = "LONGTEXT")
    private String fieldMappingJson;

    public Long getId() {
        return id;
    }

    public String getFacilityCode() {
        return facilityCode;
    }

    public void setFacilityCode(String facilityCode) {
        this.facilityCode = facilityCode;
    }

    public String getFacilityName() {
        return facilityName;
    }

    public void setFacilityName(String facilityName) {
        this.facilityName = facilityName;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getSegmentCodes() {
        return segmentCodes;
    }

    public void setSegmentCodes(String segmentCodes) {
        this.segmentCodes = segmentCodes;
    }

    public boolean isRevolving() {
        return revolving;
    }

    public void setRevolving(boolean revolving) {
        this.revolving = revolving;
    }

    public boolean isFunded() {
        return funded;
    }

    public void setFunded(boolean funded) {
        this.funded = funded;
    }

    public boolean isNonFunded() {
        return nonFunded;
    }

    public void setNonFunded(boolean nonFunded) {
        this.nonFunded = nonFunded;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getFieldMappingJson() {
        return fieldMappingJson;
    }

    public void setFieldMappingJson(String fieldMappingJson) {
        this.fieldMappingJson = fieldMappingJson;
    }
}
