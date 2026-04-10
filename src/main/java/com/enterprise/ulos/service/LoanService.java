package com.enterprise.ulos.service;

import com.enterprise.ulos.domain.loan.LoanEntity;
import com.enterprise.ulos.domain.loan.LoanResponse;
import com.enterprise.ulos.domain.workflow.StartLoanWorkflowRequest;
import com.enterprise.ulos.repository.LoanRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class LoanService {

    private static final String PROCESS_DEFINITION_KEY = "loanApproval";

    private final LoanRepository loanRepository;

    public LoanService(LoanRepository loanRepository) {
        this.loanRepository = loanRepository;
    }

    public String buildBusinessKey(StartLoanWorkflowRequest request) {
        return "LOAN-" + request.loanId();
    }

    public Map<String, Object> buildLoanVariables(StartLoanWorkflowRequest request) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("loanId", request.loanId());
        variables.put("customerId", request.customerId());
        variables.put("customerName", request.customerName());
        variables.put("loanAmount", request.loanAmount());
        variables.put("tenorMonths", request.tenorMonths());
        variables.put("status", "SUBMITTED");
        return variables;
    }

    public LoanEntity createSubmittedLoan(StartLoanWorkflowRequest request, String businessKey) {
        if (loanRepository.existsById(request.loanId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Loan already exists: " + request.loanId());
        }
        LoanEntity loan = new LoanEntity();
        loan.setLoanId(request.loanId());
        loan.setCustomerId(request.customerId());
        loan.setCustomerName(request.customerName());
        loan.setLoanAmount(request.loanAmount());
        loan.setTenorMonths(request.tenorMonths());
        loan.setBusinessKey(businessKey);
        loan.setProcessDefinitionKey(PROCESS_DEFINITION_KEY);
        loan.setStatus("SUBMITTED");
        return loanRepository.save(loan);
    }

    public LoanEntity markWorkflowStartFailed(String loanId) {
        LoanEntity loan = getLoanEntity(loanId);
        loan.setStatus("START_FAILED");
        return loanRepository.save(loan);
    }

    public LoanEntity markWorkflowStarted(String loanId, String processInstanceId) {
        LoanEntity loan = getLoanEntity(loanId);
        loan.setProcessInstanceId(processInstanceId);
        loan.setStatus("IN_REVIEW");
        return loanRepository.save(loan);
    }

    public void updateStatusByProcessInstanceId(String processInstanceId, String status) {
        LoanEntity loan = loanRepository.findByProcessInstanceId(processInstanceId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Loan not found for process instance: " + processInstanceId));
        loan.setStatus(status);
        loanRepository.save(loan);
    }

    public List<LoanResponse> getLoans() {
        return loanRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(LoanEntity::getCreatedAt).reversed())
                .map(this::toResponse)
                .toList();
    }

    public LoanResponse getLoan(String loanId) {
        return toResponse(getLoanEntity(loanId));
    }

    private LoanEntity getLoanEntity(String loanId) {
        return loanRepository.findById(loanId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Loan not found: " + loanId));
    }

    private LoanResponse toResponse(LoanEntity loan) {
        return new LoanResponse(
                loan.getLoanId(),
                loan.getCustomerId(),
                loan.getCustomerName(),
                loan.getLoanAmount(),
                loan.getTenorMonths(),
                loan.getBusinessKey(),
                loan.getProcessInstanceId(),
                loan.getProcessDefinitionKey(),
                loan.getStatus(),
                loan.getCreatedAt(),
                loan.getUpdatedAt()
        );
    }
}
