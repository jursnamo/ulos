package com.enterprise.ulos.los.model;

import com.fasterxml.jackson.databind.JsonNode;

import java.math.BigDecimal;
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
            String remarks
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
            String remarks,
            List<WorkflowApiModels.WorkflowHistoryItem> workflowHistory,
            LocalDateTime createdAt,
            LocalDateTime updatedAt,
            LocalDateTime submittedAt
    ) {
    }
}
