package com.enterprise.ulos.service;

import com.enterprise.ulos.domain.workflow.CompleteTaskRequest;
import com.enterprise.ulos.domain.workflow.StartLoanWorkflowRequest;
import com.enterprise.ulos.domain.workflow.WorkflowStartResponse;
import com.enterprise.ulos.domain.workflow.WorkflowTaskResponse;
import com.enterprise.ulos.workflow.WorkflowService;
import org.flowable.engine.runtime.ProcessInstance;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class LoanWorkflowOrchestrationService {

    private static final String LOAN_APPROVAL_PROCESS_KEY = "loanApproval";

    private final LoanService loanService;
    private final CustomerService customerService;
    private final WorkflowService workflowService;

    public LoanWorkflowOrchestrationService(
            LoanService loanService,
            CustomerService customerService,
            WorkflowService workflowService
    ) {
        this.loanService = loanService;
        this.customerService = customerService;
        this.workflowService = workflowService;
    }

    public WorkflowStartResponse startLoanWorkflow(StartLoanWorkflowRequest request) {
        Map<String, Object> variables = new HashMap<>(loanService.buildLoanVariables(request));
        variables.putAll(customerService.loadCustomerContext(request.customerId(), request.customerName()));

        String businessKey = loanService.buildBusinessKey(request);
        loanService.createSubmittedLoan(request, businessKey);
        ProcessInstance processInstance;
        try {
            processInstance = workflowService.startProcess(LOAN_APPROVAL_PROCESS_KEY, businessKey, variables);
        } catch (RuntimeException exception) {
            loanService.markWorkflowStartFailed(request.loanId());
            throw exception;
        }
        loanService.markWorkflowStarted(request.loanId(), processInstance.getId());

        return new WorkflowStartResponse(
                processInstance.getId(),
                processInstance.getProcessDefinitionKey(),
                businessKey
        );
    }

    public List<WorkflowTaskResponse> getTasks() {
        return workflowService.getTasks();
    }

    public void completeTask(String taskId, CompleteTaskRequest request) {
        Map<String, Object> variables = request == null || request.variables() == null
                ? Map.of()
                : request.variables();
        String processInstanceId = workflowService.getProcessInstanceIdForTask(taskId);
        workflowService.completeTask(taskId, variables);
        loanService.updateStatusByProcessInstanceId(processInstanceId, resolveLoanStatus(variables));
    }

    private String resolveLoanStatus(Map<String, Object> variables) {
        Object approved = variables.get("approved");
        if (approved instanceof Boolean decision) {
            return decision ? "APPROVED" : "REJECTED";
        }
        return "COMPLETED";
    }
}
