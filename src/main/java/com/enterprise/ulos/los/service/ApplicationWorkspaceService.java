package com.enterprise.ulos.los.service;

import com.enterprise.ulos.los.entity.ApplicationWorkspaceEntity;
import com.enterprise.ulos.los.entity.CustomerEntity;
import com.enterprise.ulos.los.entity.WorkflowHistoryEntity;
import com.enterprise.ulos.los.model.ApplicationApiModels;
import com.enterprise.ulos.los.model.CustomerApiModels;
import com.enterprise.ulos.los.model.WorkflowApiModels;
import com.enterprise.ulos.los.repository.ApplicationWorkspaceRepository;
import com.enterprise.ulos.los.repository.WorkflowHistoryRepository;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ApplicationWorkspaceService {

    private final ApplicationWorkspaceRepository applicationWorkspaceRepository;
    private final WorkflowHistoryRepository workflowHistoryRepository;
    private final CustomerPortfolioService customerPortfolioService;
    private final JsonSectionMapper jsonSectionMapper;

    public ApplicationWorkspaceService(
            ApplicationWorkspaceRepository applicationWorkspaceRepository,
            WorkflowHistoryRepository workflowHistoryRepository,
            CustomerPortfolioService customerPortfolioService,
            JsonSectionMapper jsonSectionMapper
    ) {
        this.applicationWorkspaceRepository = applicationWorkspaceRepository;
        this.workflowHistoryRepository = workflowHistoryRepository;
        this.customerPortfolioService = customerPortfolioService;
        this.jsonSectionMapper = jsonSectionMapper;
    }

    public ApplicationApiModels.ApplicationWorkspaceResponse saveDraft(ApplicationApiModels.ApplicationWorkspaceRequest request) {
        validateRequest(request);

        String applicationId = request.applicationId() == null || request.applicationId().isBlank()
                ? generateApplicationId()
                : request.applicationId().trim();

        ApplicationWorkspaceEntity entity = applicationWorkspaceRepository.findByApplicationId(applicationId)
                .orElseGet(ApplicationWorkspaceEntity::new);
        CustomerEntity customer = customerPortfolioService.findEntityByCif(request.customerCif());

        entity.setApplicationId(applicationId);
        entity.setCustomer(customer);
        entity.setApplicationType(request.applicationType().trim());
        entity.setSegmentCode(defaultValue(request.segmentCode(), "COMBA"));
        entity.setSetupJson(jsonSectionMapper.toJson(request.setupData()));
        entity.setComplianceJson(jsonSectionMapper.toJson(request.complianceData()));
        entity.setFacilitiesJson(jsonSectionMapper.toJson(request.facilities()));
        entity.setCollateralsJson(jsonSectionMapper.toJson(request.collaterals()));
        entity.setLinksJson(jsonSectionMapper.toJson(request.links()));
        entity.setDrawdownConditionsJson(jsonSectionMapper.toJson(request.drawdownConditions()));
        entity.setGlobalTcJson(jsonSectionMapper.toJson(request.globalTcItems()));
        entity.setTboJson(jsonSectionMapper.toJson(request.tboOpportunities()));
        entity.setTboDocsJson(jsonSectionMapper.toJson(request.tboDocuments()));
        entity.setFinancialsJson(jsonSectionMapper.toJson(request.financials()));
        entity.setSlikJson(jsonSectionMapper.toJson(request.slikSubjects()));
        entity.setLinkedSlikCheckRef(request.linkedSlikCheckRef());
        entity.setLinkedCollateralCheckRef(request.linkedCollateralCheckRef());
        entity.setRemarks(request.remarks());
        entity.setProposedExposure(jsonSectionMapper.sumExposure(request.facilities()));

        if (entity.getWorkflowStatus() == null || entity.getWorkflowStatus().isBlank()) {
            entity.setWorkflowStatus("DRAFT");
        }
        if (entity.getCurrentStage() == null || entity.getCurrentStage().isBlank()) {
            entity.setCurrentStage("Origination");
        }

        return toWorkspace(applicationWorkspaceRepository.save(entity));
    }

    @Transactional(readOnly = true)
    public List<ApplicationApiModels.ApplicationSummaryResponse> list() {
        return applicationWorkspaceRepository.findAll().stream()
                .sorted(Comparator.comparing(ApplicationWorkspaceEntity::getUpdatedAt).reversed())
                .map(this::toSummary)
                .toList();
    }

    @Transactional(readOnly = true)
    public ApplicationApiModels.ApplicationWorkspaceResponse get(String applicationId) {
        return toWorkspace(getEntity(applicationId));
    }

    public SubmissionContext prepareSubmission(String applicationId) {
        ApplicationWorkspaceEntity entity = getEntity(applicationId);
        if (!"DRAFT".equalsIgnoreCase(entity.getWorkflowStatus())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Only DRAFT application can be submitted");
        }
        entity.setWorkflowStatus("SUBMITTED");
        entity.setCurrentStage("Queueing Workflow");
        entity.setSubmittedAt(LocalDateTime.now());
        ApplicationWorkspaceEntity saved = applicationWorkspaceRepository.save(entity);

        appendHistory(saved, "SUBMISSION", "SUBMITTED", "system", "Application submitted to workflow");
        return new SubmissionContext(saved);
    }

    public void markWorkflowStarted(String applicationId, String processInstanceId, String currentStage) {
        ApplicationWorkspaceEntity entity = getEntity(applicationId);
        entity.setProcessInstanceId(processInstanceId);
        entity.setWorkflowStatus("UNDER_REVIEW");
        entity.setCurrentStage(currentStage);
        applicationWorkspaceRepository.save(entity);
    }

    public void updateWorkflowState(String applicationId, String status, String currentStage) {
        ApplicationWorkspaceEntity entity = getEntity(applicationId);
        entity.setWorkflowStatus(status);
        entity.setCurrentStage(currentStage);
        applicationWorkspaceRepository.save(entity);
    }

    public void appendHistory(String applicationId, String stage, String decision, String actor, String notes) {
        appendHistory(getEntity(applicationId), stage, decision, actor, notes);
    }

    public void appendHistory(ApplicationWorkspaceEntity entity, String stage, String decision, String actor, String notes) {
        WorkflowHistoryEntity history = new WorkflowHistoryEntity();
        history.setApplication(entity);
        history.setStageCode(stage);
        history.setDecisionCode(decision);
        history.setActor(actor == null || actor.isBlank() ? "SYSTEM" : actor);
        history.setNotes(notes);
        history.setDecidedAt(LocalDateTime.now());
        workflowHistoryRepository.save(history);
    }

    @Transactional(readOnly = true)
    public ApplicationWorkspaceEntity findByProcessInstanceId(String processInstanceId) {
        return applicationWorkspaceRepository.findByProcessInstanceId(processInstanceId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Application not found for process instance: " + processInstanceId));
    }

    @Transactional(readOnly = true)
    public WorkflowApiModels.DashboardSummaryResponse buildDashboardSummary(long openTasks) {
        List<ApplicationWorkspaceEntity> applications = applicationWorkspaceRepository.findAll();
        BigDecimal exposure = applications.stream()
                .map(ApplicationWorkspaceEntity::getProposedExposure)
                .filter(value -> value != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new WorkflowApiModels.DashboardSummaryResponse(
                customerPortfolioService.list().size(),
                applications.size(),
                applications.stream().filter(item -> "DRAFT".equalsIgnoreCase(item.getWorkflowStatus())).count(),
                applications.stream().filter(item -> "UNDER_REVIEW".equalsIgnoreCase(item.getWorkflowStatus())).count(),
                applications.stream().filter(item -> "APPROVED".equalsIgnoreCase(item.getWorkflowStatus())).count(),
                applications.stream().filter(item -> "REJECTED".equalsIgnoreCase(item.getWorkflowStatus())).count(),
                openTasks,
                exposure
        );
    }

    @Transactional(readOnly = true)
    public ApplicationWorkspaceEntity getEntity(String applicationId) {
        return applicationWorkspaceRepository.findByApplicationId(applicationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Application not found: " + applicationId));
    }

    private ApplicationApiModels.ApplicationSummaryResponse toSummary(ApplicationWorkspaceEntity entity) {
        return new ApplicationApiModels.ApplicationSummaryResponse(
                entity.getApplicationId(),
                entity.getCustomer().getCifNumber(),
                entity.getCustomer().getCompanyName(),
                entity.getApplicationType(),
                entity.getSegmentCode(),
                entity.getWorkflowStatus(),
                entity.getCurrentStage(),
                entity.getProposedExposure() == null ? BigDecimal.ZERO : entity.getProposedExposure(),
                entity.getUpdatedAt()
        );
    }

    private ApplicationApiModels.ApplicationWorkspaceResponse toWorkspace(ApplicationWorkspaceEntity entity) {
        List<WorkflowApiModels.WorkflowHistoryItem> history = workflowHistoryRepository.findByApplicationOrderByDecidedAtAsc(entity)
                .stream()
                .map(item -> new WorkflowApiModels.WorkflowHistoryItem(
                        item.getStageCode(),
                        item.getDecisionCode(),
                        item.getActor(),
                        item.getNotes(),
                        item.getDecidedAt()
                ))
                .toList();

        return new ApplicationApiModels.ApplicationWorkspaceResponse(
                entity.getApplicationId(),
                new CustomerApiModels.CustomerSummaryResponse(
                        entity.getCustomer().getCifNumber(),
                        entity.getCustomer().getCompanyName(),
                        entity.getCustomer().getSector(),
                        entity.getCustomer().getLocation(),
                        entity.getCustomer().getStatus(),
                        entity.getCustomer().getUpdatedAt()
                ),
                entity.getApplicationType(),
                entity.getSegmentCode(),
                entity.getWorkflowStatus(),
                entity.getCurrentStage(),
                entity.getProcessInstanceId(),
                entity.getProposedExposure() == null ? BigDecimal.ZERO : entity.getProposedExposure(),
                parseNode(entity.getSetupJson()),
                parseNode(entity.getComplianceJson()),
                parseNode(entity.getFacilitiesJson()),
                parseNode(entity.getCollateralsJson()),
                parseNode(entity.getLinksJson()),
                parseNode(entity.getDrawdownConditionsJson()),
                parseNode(entity.getGlobalTcJson()),
                parseNode(entity.getTboJson()),
                parseNode(entity.getTboDocsJson()),
                parseNode(entity.getFinancialsJson()),
                parseNode(entity.getSlikJson()),
                entity.getLinkedSlikCheckRef(),
                entity.getLinkedCollateralCheckRef(),
                entity.getRemarks(),
                history,
                entity.getCreatedAt(),
                entity.getUpdatedAt(),
                entity.getSubmittedAt()
        );
    }

    private JsonNode parseNode(String value) {
        return jsonSectionMapper.fromJson(value);
    }

    private void validateRequest(ApplicationApiModels.ApplicationWorkspaceRequest request) {
        if (request == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Application payload is required");
        }
        if (request.customerCif() == null || request.customerCif().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "customerCif is required");
        }
        if (request.applicationType() == null || request.applicationType().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "applicationType is required");
        }
    }

    private String generateApplicationId() {
        return "APP-" + LocalDateTime.now().toLocalDate().toString().replace("-", "") + "-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }

    private String defaultValue(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value.trim();
    }

    public record SubmissionContext(ApplicationWorkspaceEntity entity) {
    }
}
