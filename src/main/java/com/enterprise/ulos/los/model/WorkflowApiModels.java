package com.enterprise.ulos.los.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public final class WorkflowApiModels {

    private WorkflowApiModels() {
    }

    public record TaskDecisionRequest(
            String decision,
            String decisionNotes,
            String decidedBy
    ) {
    }

    public record WorkflowLaunchResponse(
            String applicationId,
            String processInstanceId,
            String processDefinitionKey,
            String businessKey,
            String workflowStatus,
            String currentApprovalTier
    ) {
    }

    public record ApprovalTaskResponse(
            String taskId,
            String taskDefinitionKey,
            String taskName,
            String assignee,
            String processInstanceId,
            String applicationId,
            String customerCif,
            String companyName,
            BigDecimal proposedExposure,
            String workflowStatus,
            String approvalTier
    ) {
    }

    public record ApprovalHistoryItem(
            String stage,
            String decision,
            String actor,
            String notes,
            LocalDateTime decidedAt
    ) {
    }

    public record DashboardSummaryResponse(
            long totalCustomers,
            long totalApplications,
            long draftApplications,
            long inReviewApplications,
            long approvedApplications,
            long rejectedApplications,
            long hardStopApplications,
            long openTasks,
            BigDecimal totalProposedExposure,
            long pendingTboCount
    ) {
    }
}
