package com.enterprise.ulos.los.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.enterprise.ulos.los.model.RuleApiModels;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public final class ApplicationApiModels {

    private ApplicationApiModels() {
    }

    public record ApplicationWorkspaceRequest(
            String applicationId,
            String customerCif,
            String applicationType,
            String segmentCode,
            JsonNode setupData,
            JsonNode complianceData,
            JsonNode facilities,
            JsonNode collaterals,
            JsonNode links,
            JsonNode drawdownConditions,
            JsonNode globalTcItems,
            JsonNode tboOpportunities,
            JsonNode tboDocuments,
            JsonNode financials,
            JsonNode slikSubjects,
            String linkedSlikCheckRef,
            String linkedCollateralCheckRef,
            String remarks
    ) {
    }

    public record CreditApplicationRequest(
            String applicationId,
            String customerCif,
            LocalDate applicationDate,
            String applicationType,
            String rmName,
            String branchName,
            String regionName,
            String cbcName,
            String groupRelationshipStatus,
            BigDecimal coreCapitalBank,
            BigDecimal maxLendingLimitPercentage,
            BigDecimal existingExposureGroup,
            String biSectorCode,
            String subSectorDescription,
            String industryOutlook,
            String esgGreenFinancingStatus,
            String justificationNote,
            List<FacilityItem> facilities,
            List<CollateralItem> collaterals,
            List<TboItem> tboItems,
            List<CovenantItem> covenants
    ) {
    }

    public record ApplicationSummaryResponse(
            String applicationId,
            String customerCif,
            String customerName,
            String applicationType,
            String segmentCode,
            String workflowStatus,
            String currentStage,
            BigDecimal proposedExposure,
            LocalDateTime updatedAt
    ) {
    }

    public record ApplicationWorkspaceResponse(
            String applicationId,
            CustomerApiModels.CustomerSummaryResponse customer,
            String applicationType,
            String segmentCode,
            String workflowStatus,
            String currentStage,
            String processInstanceId,
            BigDecimal proposedExposure,
            JsonNode setupData,
            JsonNode complianceData,
            JsonNode facilities,
            JsonNode collaterals,
            JsonNode links,
            JsonNode drawdownConditions,
            JsonNode globalTcItems,
            JsonNode tboOpportunities,
            JsonNode tboDocuments,
            JsonNode financials,
            JsonNode slikSubjects,
            String linkedSlikCheckRef,
            String linkedCollateralCheckRef,
            String remarks,
            List<WorkflowApiModels.WorkflowHistoryItem> workflowHistory,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            LocalDateTime submittedAt
    ) {
    }

    public record CreditApplicationResponse(
            String applicationId,
            LocalDate applicationDate,
            String applicationType,
            String rmName,
            String branchName,
            String regionName,
            String cbcName,
            String groupRelationshipStatus,
            BigDecimal coreCapitalBank,
            BigDecimal maxLendingLimitPercentage,
            BigDecimal existingExposureGroup,
            BigDecimal proposedExposure,
            BigDecimal availableLimit,
            String biSectorCode,
            String subSectorDescription,
            String industryOutlook,
            String esgGreenFinancingStatus,
            String justificationNote,
            String workflowStatus,
            String currentApprovalTier,
            String processInstanceId,
            CustomerApiModels.CustomerSummaryResponse customer,
            CustomerApiModels.QuantitativeAnalysisSnapshot latestAnalysis,
            BigDecimal collateralCoverage,
            List<FacilityItem> facilities,
            List<CollateralItem> collaterals,
            List<TboItem> tboItems,
            List<CovenantItem> covenants,
            RuleApiModels.RuleEvaluationResponse ruleEvaluation,
            List<WorkflowApiModels.ApprovalHistoryItem> approvalHistories,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
    }

    public record FacilityItem(
            String facilityCode,
            String facilityName,
            String revolvingStatus,
            String currency,
            BigDecimal limitAmount,
            Integer tenorMonths,
            LocalDate maturityDate,
            String interestRateType,
            BigDecimal interestRate,
            BigDecimal provisionFee,
            BigDecimal adminFee,
            BigDecimal commitmentFee,
            BigDecimal penaltyFee,
            String repaymentType,
            String purposeOfLoan
    ) {
    }

    public record CollateralItem(
            String collateralId,
            String collateralType,
            String ownerName,
            String locationAddress,
            LocalDate appraisalDate,
            String appraiserName,
            BigDecimal marketValue,
            BigDecimal liquidationValue,
            BigDecimal marginOfAdvancePercentage,
            BigDecimal bankableValue,
            String legalDocumentInfo,
            LocalDate legalDocumentExpiryDate,
            String insuranceName,
            BigDecimal insuranceCoverageValue,
            LocalDate insuranceExpiryDate,
            List<String> linkedFacilityCodes
    ) {
    }

    public record TboItem(
            String tboId,
            String linkedFacilityCode,
            String tboCategory,
            String documentRequirement,
            LocalDate dueDate,
            String status,
            String pic
    ) {
    }

    public record CovenantItem(
            String covenantId,
            String covenantType,
            String description,
            String testingFrequency,
            LocalDate measurementDate,
            String status,
            String penaltyDetails
    ) {
    }
}
