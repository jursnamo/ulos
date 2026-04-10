package com.enterprise.ulos.los.service;

import com.enterprise.ulos.los.entity.CreditApplicationEntity;
import com.enterprise.ulos.los.model.WorkflowApiModels;
import com.enterprise.ulos.los.security.SecurityUtils;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class CorporateApprovalWorkflowService {

    private static final String PROCESS_KEY = "corporateCreditApproval";

    private final RuntimeService runtimeService;
    private final TaskService taskService;
    private final CreditApplicationService creditApplicationService;
    private final SecurityUtils securityUtils;

    public CorporateApprovalWorkflowService(
            RuntimeService runtimeService,
            TaskService taskService,
            CreditApplicationService creditApplicationService,
            SecurityUtils securityUtils
    ) {
        this.runtimeService = runtimeService;
        this.taskService = taskService;
        this.creditApplicationService = creditApplicationService;
        this.securityUtils = securityUtils;
    }

    public WorkflowApiModels.WorkflowLaunchResponse launch(String applicationId) {
        CreditApplicationService.SubmissionContext context = creditApplicationService.prepareSubmission(applicationId);

        Map<String, Object> variables = new HashMap<>();
        variables.put("applicationId", context.entity().getApplicationId());
        variables.put("customerCif", context.customer().cifNumber());
        variables.put("companyName", context.customer().companyName());
        variables.put("rmName", context.entity().getRmUsername());
        variables.put("proposedExposure", context.entity().getProposedExposure());
        variables.put("hardStop", context.ruleEvaluation().hardStop());
        variables.put("requiresRegionalApproval", tierRank(context.ruleEvaluation().requiredApprovalTier()) >= tierRank("REGIONAL_HEAD"));
        variables.put("requiresCommitteeApproval", tierRank(context.ruleEvaluation().requiredApprovalTier()) >= tierRank("CREDIT_COMMITTEE"));
        variables.put("requiresBoardApproval", tierRank(context.ruleEvaluation().requiredApprovalTier()) >= tierRank("BOARD_OF_DIRECTORS"));
        variables.put("analystAssignee", "ANALYST");
        variables.put("baseApprover", defaultValue(context.ruleEvaluation().baseApprover(), "BRANCH_MANAGER"));
        variables.put("regionalApprover", defaultValue(context.ruleEvaluation().regionalApprover(), "REGIONAL_HEAD"));
        variables.put("committeeApprover", defaultValue(context.ruleEvaluation().committeeApprover(), "CREDIT_COMMITTEE"));
        variables.put("boardApprover", defaultValue(context.ruleEvaluation().boardApprover(), "BOARD_OF_DIRECTORS"));

        String businessKey = "APPLICATION-" + context.entity().getApplicationId();
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey(PROCESS_KEY, businessKey, variables);

        creditApplicationService.appendHistory(
                context.entity().getApplicationId(),
                new WorkflowApiModels.ApprovalHistoryItem(
                        "SUBMISSION",
                        "SUBMITTED",
                        defaultValue(context.entity().getRmUsername(), "SYSTEM"),
                        "Application submitted into approval workflow",
                        LocalDateTime.now()
                )
        );

        Task nextTask = getNextTask(processInstance.getId());
        if (nextTask == null) {
            String status = context.ruleEvaluation().hardStop() ? "HARD_STOP" : "APPROVED";
            String tier = context.ruleEvaluation().hardStop() ? "HARD_STOP" : "COMPLETED";
            creditApplicationService.markWorkflowStarted(context.entity().getApplicationId(), processInstance.getId(), status, tier);

            if (context.ruleEvaluation().hardStop()) {
                creditApplicationService.appendHistory(
                        context.entity().getApplicationId(),
                        new WorkflowApiModels.ApprovalHistoryItem(
                                "RULE_ENGINE",
                                "HARD_STOP",
                                "SYSTEM",
                                "Application stopped automatically because hard stop rule was triggered",
                                LocalDateTime.now()
                        )
                );
            }
        } else {
            creditApplicationService.markWorkflowStarted(context.entity().getApplicationId(), processInstance.getId(), "IN_REVIEW", stageLabel(nextTask.getTaskDefinitionKey()));
        }

        CreditApplicationEntity refreshed = creditApplicationService.findByProcessInstanceId(processInstance.getId());
        return new WorkflowApiModels.WorkflowLaunchResponse(
                refreshed.getApplicationId(),
                processInstance.getId(),
                PROCESS_KEY,
                businessKey,
                refreshed.getWorkflowStatus(),
                refreshed.getCurrentApprovalTier()
        );
    }

    @Transactional(readOnly = true)
    public List<WorkflowApiModels.ApprovalTaskResponse> getTasks() {
        List<String> visibleRoles = securityUtils.currentRoles();
        boolean admin = securityUtils.hasAnyRole("ADMIN");

        return taskService.createTaskQuery()
                .active()
                .orderByTaskCreateTime()
                .desc()
                .list()
                .stream()
                .filter(task -> admin || visibleRoles.contains(defaultValue(task.getAssignee(), "")) || securityUtils.currentUsername().equalsIgnoreCase(defaultValue(task.getAssignee(), "")))
                .map(this::toTaskResponse)
                .toList();
    }

    public void completeTask(String taskId, WorkflowApiModels.TaskDecisionRequest request) {
        Task task = getTask(taskId);
        validateTaskAccess(task);

        String decision = normalizeDecision(request == null ? null : request.decision());
        boolean approved = "APPROVE".equals(decision);
        String actor = request != null && request.decidedBy() != null && !request.decidedBy().isBlank()
                ? request.decidedBy().trim()
                : securityUtils.currentUsername();
        String notes = request == null ? null : request.decisionNotes();

        String applicationId = String.valueOf(runtimeService.getVariable(task.getProcessInstanceId(), "applicationId"));
        taskService.complete(taskId, Map.of(
                "approved", approved,
                "lastDecisionBy", actor,
                "lastDecisionNotes", defaultValue(notes, "")
        ));

        creditApplicationService.appendHistory(
                applicationId,
                new WorkflowApiModels.ApprovalHistoryItem(stageLabel(task.getTaskDefinitionKey()), decision, actor, notes, LocalDateTime.now())
        );

        Task nextTask = getNextTask(task.getProcessInstanceId());
        if (nextTask == null) {
            creditApplicationService.updateWorkflowState(applicationId, approved ? "APPROVED" : "REJECTED", "COMPLETED");
        } else {
            creditApplicationService.updateWorkflowState(applicationId, "IN_REVIEW", stageLabel(nextTask.getTaskDefinitionKey()));
        }
    }

    @Transactional(readOnly = true)
    public long openTaskCount() {
        return taskService.createTaskQuery().active().count();
    }

    private WorkflowApiModels.ApprovalTaskResponse toTaskResponse(Task task) {
        CreditApplicationEntity application = creditApplicationService.findByProcessInstanceId(task.getProcessInstanceId());
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

    private Task getNextTask(String processInstanceId) {
        return taskService.createTaskQuery().processInstanceId(processInstanceId).active().singleResult();
    }

    private void validateTaskAccess(Task task) {
        if (securityUtils.hasAnyRole("ADMIN")) {
            return;
        }
        String assignee = defaultValue(task.getAssignee(), "");
        if (securityUtils.currentRoles().contains(assignee) || securityUtils.currentUsername().equalsIgnoreCase(assignee)) {
            return;
        }
        throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not allowed to complete this task");
    }

    private String normalizeDecision(String decision) {
        if (decision == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Decision is required");
        }
        String normalized = decision.trim().toUpperCase();
        if (!"APPROVE".equals(normalized) && !"REJECT".equals(normalized)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Decision must be APPROVE or REJECT");
        }
        return normalized;
    }

    private String stageLabel(String taskDefinitionKey) {
        return switch (taskDefinitionKey) {
            case "analystReviewTask" -> "ANALYST_REVIEW";
            case "branchApprovalTask" -> "BRANCH_MANAGER";
            case "regionalApprovalTask" -> "REGIONAL_HEAD";
            case "committeeApprovalTask" -> "CREDIT_COMMITTEE";
            case "boardApprovalTask" -> "BOARD_OF_DIRECTORS";
            default -> "IN_REVIEW";
        };
    }

    private int tierRank(String tier) {
        return switch (defaultValue(tier, "").toUpperCase()) {
            case "REGIONAL_HEAD" -> 2;
            case "CREDIT_COMMITTEE" -> 3;
            case "BOARD_OF_DIRECTORS" -> 4;
            case "BRANCH_MANAGER" -> 1;
            default -> 0;
        };
    }

    private String defaultValue(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }
}
