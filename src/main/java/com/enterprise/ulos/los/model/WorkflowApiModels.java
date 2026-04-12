package com.enterprise.ulos.los.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public final class WorkflowApiModels {

    private WorkflowApiModels() {
    }

    public record WorkflowLaunchResponse(
            String applicationId,
            String processInstanceId,
            String processDefinitionKey,
            String businessKey,
            String workflowStatus,
            String currentStage
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
            String approvalStage
    ) {
    }

    public record TaskDecisionRequest(
            String decision,
            String decidedBy,
            String decisionNotes
    ) {
    }

    public record WorkflowHistoryItem(
            String stage,
            String decision,
            String actor,
            String notes,
            LocalDateTime decidedAt
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
            long underReviewApplications,
            long approvedApplications,
            long rejectedApplications,
            long hardStopApplications,
            long openTasks,
            BigDecimal totalExposure,
            long pendingTboCount
    ) {
        public DashboardSummaryResponse(
                long totalCustomers,
                long totalApplications,
                long draftApplications,
                long underReviewApplications,
                long approvedApplications,
                long rejectedApplications,
                long openTasks,
                BigDecimal totalExposure
        ) {
            this(
                    totalCustomers,
                    totalApplications,
                    draftApplications,
                    underReviewApplications,
                    approvedApplications,
                    rejectedApplications,
                    0,
                    openTasks,
                    totalExposure,
                    0
            );
        }
    }
}
