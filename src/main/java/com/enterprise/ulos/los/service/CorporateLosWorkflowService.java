package com.enterprise.ulos.los.service;

import com.enterprise.ulos.los.entity.ApplicationWorkspaceEntity;
import com.enterprise.ulos.los.model.WorkflowApiModels;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class CorporateLosWorkflowService {

    private static final String PROCESS_KEY = "corporateLosApproval";

    private final RuntimeService runtimeService;
    private final TaskService taskService;
    private final ApplicationWorkspaceService applicationWorkspaceService;

    public CorporateLosWorkflowService(
            RuntimeService runtimeService,
            TaskService taskService,
            ApplicationWorkspaceService applicationWorkspaceService
    ) {
        this.runtimeService = runtimeService;
        this.taskService = taskService;
        this.applicationWorkspaceService = applicationWorkspaceService;
    }

    public WorkflowApiModels.WorkflowLaunchResponse launch(String applicationId) {
        ApplicationWorkspaceService.SubmissionContext context = applicationWorkspaceService.prepareSubmission(applicationId);
        ApplicationWorkspaceEntity application = context.entity();

        Map<String, Object> variables = new HashMap<>();
        variables.put("applicationId", application.getApplicationId());
        variables.put("customerCif", application.getCustomer().getCifNumber());
        variables.put("customerName", application.getCustomer().getCompanyName());
        variables.put("proposedExposure", application.getProposedExposure());
        variables.put("approved", true);

        String businessKey = "LOS-APPLICATION-" + application.getApplicationId();
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(PROCESS_KEY, businessKey, variables);
        Task firstTask = nextTask(processInstance.getId());

        String stage = firstTask == null ? "COMPLETED" : stageLabel(firstTask.getTaskDefinitionKey());
        applicationWorkspaceService.markWorkflowStarted(application.getApplicationId(), processInstance.getId(), stage);

        return new WorkflowApiModels.WorkflowLaunchResponse(
                application.getApplicationId(),
                processInstance.getId(),
                processInstance.getProcessDefinitionKey(),
                businessKey,
                "UNDER_REVIEW",
                stage
        );
    }

    @Transactional(readOnly = true)
    public List<WorkflowApiModels.ApprovalTaskResponse> tasks() {
        return taskService.createTaskQuery()
                .active()
                .orderByTaskCreateTime()
                .desc()
                .list()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public void complete(String taskId, WorkflowApiModels.TaskDecisionRequest request) {
        Task task = getTask(taskId);
        String decision = normalizeDecision(request == null ? null : request.decision());
        boolean approved = "APPROVE".equals(decision);
        String actor = request == null || request.decidedBy() == null || request.decidedBy().isBlank()
                ? "SYSTEM_USER"
                : request.decidedBy().trim();

        taskService.complete(task.getId(), Map.of(
                "approved", approved,
                "decisionBy", actor,
                "decisionNotes", request == null ? "" : defaultValue(request.decisionNotes(), "")
        ));

        ApplicationWorkspaceEntity application = applicationWorkspaceService.findByProcessInstanceId(task.getProcessInstanceId());
        applicationWorkspaceService.appendHistory(
                application.getApplicationId(),
                stageLabel(task.getTaskDefinitionKey()),
                decision,
                actor,
                request == null ? null : request.decisionNotes()
        );

        Task nextTask = nextTask(task.getProcessInstanceId());
        if (nextTask == null) {
            String finalStatus = approved ? "APPROVED" : "REJECTED";
            applicationWorkspaceService.updateWorkflowState(application.getApplicationId(), finalStatus, "COMPLETED");
            applicationWorkspaceService.appendHistory(
                    application.getApplicationId(),
                    "WORKFLOW_END",
                    finalStatus,
                    actor,
                    "Workflow reached terminal state"
            );
            return;
        }
        applicationWorkspaceService.updateWorkflowState(application.getApplicationId(), "UNDER_REVIEW", stageLabel(nextTask.getTaskDefinitionKey()));
    }

    @Transactional(readOnly = true)
    public long openTaskCount() {
        return taskService.createTaskQuery().active().count();
    }

    private WorkflowApiModels.ApprovalTaskResponse toResponse(Task task) {
        ApplicationWorkspaceEntity application = applicationWorkspaceService.findByProcessInstanceId(task.getProcessInstanceId());
        return new WorkflowApiModels.ApprovalTaskResponse(
                task.getId(),
                task.getTaskDefinitionKey(),
                task.getName(),
                task.getAssignee(),
                task.getProcessInstanceId(),
                application.getApplicationId(),
                application.getCustomer().getCifNumber(),
                application.getCustomer().getCompanyName(),
                application.getProposedExposure(),
                application.getWorkflowStatus(),
                stageLabel(task.getTaskDefinitionKey())
        );
    }

    private Task getTask(String taskId) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found: " + taskId);
        }
        return task;
    }

    private Task nextTask(String processInstanceId) {
        return taskService.createTaskQuery().processInstanceId(processInstanceId).active().singleResult();
    }

    private String stageLabel(String taskDefinitionKey) {
        return switch (taskDefinitionKey) {
            case "analystReviewTask" -> "ANALYST_REVIEW";
            case "riskReviewTask" -> "RISK_REVIEW";
            case "committeeApprovalTask" -> "COMMITTEE_APPROVAL";
            default -> "UNDER_REVIEW";
        };
    }

    private String normalizeDecision(String decision) {
        if (decision == null || decision.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "decision is required");
        }
        String normalized = decision.trim().toUpperCase();
        if (!"APPROVE".equals(normalized) && !"REJECT".equals(normalized)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "decision must be APPROVE or REJECT");
        }
        return normalized;
    }

    private String defaultValue(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value.trim();
    }
}
