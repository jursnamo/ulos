package com.enterprise.ulos.los.service;

import com.enterprise.ulos.los.entity.AppUserEntity;
import com.enterprise.ulos.los.entity.ApplicationApprovalHistoryEntity;
import com.enterprise.ulos.los.entity.ApplicationRuleHitEntity;
import com.enterprise.ulos.los.entity.CollateralEntity;
import com.enterprise.ulos.los.entity.CovenantItemEntity;
import com.enterprise.ulos.los.entity.CreditApplicationEntity;
import com.enterprise.ulos.los.entity.CustomerPortfolioEntity;
import com.enterprise.ulos.los.entity.FacilityEntity;
import com.enterprise.ulos.los.entity.TboItemEntity;
import com.enterprise.ulos.los.model.ApplicationApiModels;
import com.enterprise.ulos.los.model.CustomerApiModels;
import com.enterprise.ulos.los.model.RuleApiModels;
import com.enterprise.ulos.los.model.WorkflowApiModels;
import com.enterprise.ulos.los.repository.AppUserRepository;
import com.enterprise.ulos.los.repository.CreditApplicationRepository;
import com.enterprise.ulos.los.repository.CustomerPortfolioRepository;
import com.enterprise.ulos.los.security.SecurityUtils;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class CreditApplicationService {

    private final CreditApplicationRepository creditApplicationRepository;
    private final CustomerPortfolioRepository customerPortfolioRepository;
    private final CustomerPortfolioService customerPortfolioService;
    private final ApprovalRuleService approvalRuleService;
    private final AppUserRepository appUserRepository;
    private final SecurityUtils securityUtils;

    public CreditApplicationService(
            CreditApplicationRepository creditApplicationRepository,
            CustomerPortfolioRepository customerPortfolioRepository,
            CustomerPortfolioService customerPortfolioService,
            ApprovalRuleService approvalRuleService,
            AppUserRepository appUserRepository,
            SecurityUtils securityUtils
    ) {
        this.creditApplicationRepository = creditApplicationRepository;
        this.customerPortfolioRepository = customerPortfolioRepository;
        this.customerPortfolioService = customerPortfolioService;
        this.approvalRuleService = approvalRuleService;
        this.appUserRepository = appUserRepository;
        this.securityUtils = securityUtils;
    }

    public ApplicationApiModels.CreditApplicationResponse saveDraft(ApplicationApiModels.CreditApplicationRequest request) {
        validateRequest(request);

        CustomerPortfolioEntity customer = customerPortfolioRepository.findById(request.customerCif())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found: " + request.customerCif()));
        CustomerApiModels.QuantitativeAnalysisSnapshot latestAnalysis = customerPortfolioService.latestStandaloneAnalysis(customer.getCifNumber());

        String applicationId = request.applicationId() == null || request.applicationId().isBlank()
                ? generateApplicationId()
                : request.applicationId().trim();

        CreditApplicationEntity entity = creditApplicationRepository.findById(applicationId)
                .orElseGet(CreditApplicationEntity::new);

        if (entity.getApplicationId() != null && !"DRAFT".equalsIgnoreCase(entity.getWorkflowStatus())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Only draft application can be updated");
        }

        entity.setApplicationId(applicationId);
        entity.setCustomer(customer);
        entity.setApplicationDate(request.applicationDate() == null ? LocalDate.now() : request.applicationDate());
        entity.setApplicationType(request.applicationType());
        entity.setRmUsername(request.rmName());
        entity.setBranchName(request.branchName());
        entity.setRegionName(request.regionName());
        entity.setCbcName(request.cbcName());
        entity.setGroupRelationshipStatus(request.groupRelationshipStatus());
        entity.setCoreCapitalBank(safe(request.coreCapitalBank()));
        entity.setMaxLendingLimitPercentage(safe(request.maxLendingLimitPercentage()));
        entity.setExistingExposureGroup(safe(request.existingExposureGroup()));
        entity.setBiSectorCode(request.biSectorCode());
        entity.setSubSectorDescription(request.subSectorDescription());
        entity.setIndustryOutlook(request.industryOutlook());
        entity.setEsgGreenFinancingStatus(request.esgGreenFinancingStatus());
        entity.setJustificationNote(request.justificationNote());
        entity.setWorkflowStatus(entity.getWorkflowStatus() == null ? "DRAFT" : entity.getWorkflowStatus());
        entity.setCurrentApprovalTier(entity.getCurrentApprovalTier() == null ? "DRAFT" : entity.getCurrentApprovalTier());
        entity.setCreatedBy(resolveCurrentUser());

        syncFacilities(entity, defaultList(request.facilities()));
        syncCollaterals(entity, defaultList(request.collaterals()));
        syncTboItems(entity, defaultList(request.tboItems()));
        syncCovenants(entity, defaultList(request.covenants()));

        BigDecimal proposedExposure = calculateProposedExposure(entity);
        BigDecimal availableLimit = calculateAvailableLimit(entity.getCoreCapitalBank(), entity.getMaxLendingLimitPercentage(), entity.getExistingExposureGroup(), proposedExposure);
        BigDecimal collateralCoverage = calculateCollateralCoverage(entity);
        RuleApiModels.RuleEvaluationResponse ruleEvaluation = approvalRuleService.evaluate(
                proposedExposure,
                availableLimit,
                latestAnalysis.debtToEquityRatio(),
                collateralCoverage,
                latestAnalysis.currentRatio()
        );

        entity.setProposedExposure(proposedExposure);
        entity.setAvailableLimit(availableLimit);
        entity.setCollateralCoverage(collateralCoverage);
        entity.setCurrentRatio(latestAnalysis.currentRatio());
        entity.setDebtToEquityRatio(latestAnalysis.debtToEquityRatio());
        syncRuleHits(entity, ruleEvaluation.hits());

        return toResponse(creditApplicationRepository.save(entity));
    }

    @Transactional(readOnly = true)
    public List<ApplicationApiModels.CreditApplicationResponse> list() {
        return creditApplicationRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(CreditApplicationEntity::getCreatedAt).reversed())
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ApplicationApiModels.CreditApplicationResponse get(String applicationId) {
        return toResponse(getEntity(applicationId));
    }

    public SubmissionContext prepareSubmission(String applicationId) {
        CreditApplicationEntity entity = getEntity(applicationId);
        if (!"DRAFT".equalsIgnoreCase(entity.getWorkflowStatus())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Only draft application can be submitted");
        }

        CustomerApiModels.QuantitativeAnalysisSnapshot latestAnalysis = customerPortfolioService.latestStandaloneAnalysis(entity.getCustomer().getCifNumber());
        BigDecimal proposedExposure = calculateProposedExposure(entity);
        BigDecimal availableLimit = calculateAvailableLimit(entity.getCoreCapitalBank(), entity.getMaxLendingLimitPercentage(), entity.getExistingExposureGroup(), proposedExposure);
        BigDecimal collateralCoverage = calculateCollateralCoverage(entity);
        RuleApiModels.RuleEvaluationResponse ruleEvaluation = approvalRuleService.evaluate(
                proposedExposure,
                availableLimit,
                latestAnalysis.debtToEquityRatio(),
                collateralCoverage,
                latestAnalysis.currentRatio()
        );

        if (ruleEvaluation.justificationRequired() && isBlank(entity.getJustificationNote())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Justification note is required because deviation or warning rule was triggered");
        }

        entity.setProposedExposure(proposedExposure);
        entity.setAvailableLimit(availableLimit);
        entity.setCollateralCoverage(collateralCoverage);
        entity.setCurrentRatio(latestAnalysis.currentRatio());
        entity.setDebtToEquityRatio(latestAnalysis.debtToEquityRatio());
        syncRuleHits(entity, ruleEvaluation.hits());

        CreditApplicationEntity saved = creditApplicationRepository.save(entity);
        return new SubmissionContext(saved, customerPortfolioService.getSummary(saved.getCustomer().getCifNumber()), latestAnalysis, ruleEvaluation);
    }

    public void markWorkflowStarted(String applicationId, String processInstanceId, String status, String currentApprovalTier) {
        CreditApplicationEntity entity = getEntity(applicationId);
        entity.setProcessInstanceId(processInstanceId);
        entity.setWorkflowStatus(status);
        entity.setCurrentApprovalTier(currentApprovalTier);
        creditApplicationRepository.save(entity);
    }

    public void updateWorkflowState(String applicationId, String status, String currentApprovalTier) {
        CreditApplicationEntity entity = getEntity(applicationId);
        entity.setWorkflowStatus(status);
        entity.setCurrentApprovalTier(currentApprovalTier);
        creditApplicationRepository.save(entity);
    }

    public void appendHistory(String applicationId, WorkflowApiModels.ApprovalHistoryItem historyItem) {
        CreditApplicationEntity entity = getEntity(applicationId);
        ApplicationApprovalHistoryEntity history = new ApplicationApprovalHistoryEntity();
        history.setApplication(entity);
        history.setStage(historyItem.stage());
        history.setDecision(historyItem.decision());
        history.setActor(historyItem.actor());
        history.setNotes(historyItem.notes());
        history.setDecidedAt(historyItem.decidedAt());
        entity.getApprovalHistories().add(history);
        creditApplicationRepository.save(entity);
    }

    @Transactional(readOnly = true)
    public CreditApplicationEntity findByProcessInstanceId(String processInstanceId) {
        return creditApplicationRepository.findByProcessInstanceId(processInstanceId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Application not found for process instance: " + processInstanceId));
    }

    @Transactional(readOnly = true)
    public WorkflowApiModels.DashboardSummaryResponse buildDashboardSummary(long openTasks) {
        List<CreditApplicationEntity> applications = creditApplicationRepository.findAll();
        BigDecimal totalProposedExposure = applications.stream()
                .map(CreditApplicationEntity::getProposedExposure)
                .map(this::safe)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        long pendingTboCount = applications.stream()
                .flatMap(application -> application.getTboItems().stream())
                .filter(item -> !"VERIFIED".equalsIgnoreCase(defaultString(item.getStatus())) && !"WAIVED".equalsIgnoreCase(defaultString(item.getStatus())))
                .count();

        return new WorkflowApiModels.DashboardSummaryResponse(
                customerPortfolioService.list().size(),
                applications.size(),
                countByStatus(applications, "DRAFT"),
                countByStatus(applications, "IN_REVIEW"),
                countByStatus(applications, "APPROVED"),
                countByStatus(applications, "REJECTED"),
                countByStatus(applications, "HARD_STOP"),
                openTasks,
                totalProposedExposure,
                pendingTboCount
        );
    }

    private void syncFacilities(CreditApplicationEntity entity, List<ApplicationApiModels.FacilityItem> items) {
        entity.getFacilities().clear();
        for (ApplicationApiModels.FacilityItem item : items) {
            FacilityEntity child = new FacilityEntity();
            child.setApplication(entity);
            child.setFacilityCode(defaultString(item.facilityCode(), "FAC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase()));
            child.setFacilityName(item.facilityName());
            child.setRevolvingStatus(item.revolvingStatus());
            child.setCurrencyCode(defaultString(item.currency(), "IDR"));
            child.setLimitAmount(safe(item.limitAmount()));
            child.setTenorMonths(item.tenorMonths());
            child.setMaturityDate(item.maturityDate());
            child.setInterestRateType(item.interestRateType());
            child.setInterestRate(safe(item.interestRate()));
            child.setProvisionFee(safe(item.provisionFee()));
            child.setAdminFee(safe(item.adminFee()));
            child.setCommitmentFee(safe(item.commitmentFee()));
            child.setPenaltyFee(safe(item.penaltyFee()));
            child.setRepaymentType(item.repaymentType());
            child.setPurposeOfLoan(item.purposeOfLoan());
            entity.getFacilities().add(child);
        }
    }

    private void syncCollaterals(CreditApplicationEntity entity, List<ApplicationApiModels.CollateralItem> items) {
        entity.getCollaterals().clear();
        for (ApplicationApiModels.CollateralItem item : items) {
            CollateralEntity child = new CollateralEntity();
            child.setApplication(entity);
            child.setCollateralId(defaultString(item.collateralId(), "COL-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase()));
            child.setCollateralType(item.collateralType());
            child.setOwnerName(item.ownerName());
            child.setLocationAddress(item.locationAddress());
            child.setAppraisalDate(item.appraisalDate());
            child.setAppraiserName(item.appraiserName());
            child.setMarketValue(safe(item.marketValue()));
            child.setLiquidationValue(safe(item.liquidationValue()));
            child.setMarginOfAdvancePercentage(safe(item.marginOfAdvancePercentage()));
            child.setBankableValue(resolveBankableValue(item));
            child.setLegalDocumentInfo(item.legalDocumentInfo());
            child.setLegalDocumentExpiryDate(item.legalDocumentExpiryDate());
            child.setInsuranceName(item.insuranceName());
            child.setInsuranceCoverageValue(safe(item.insuranceCoverageValue()));
            child.setInsuranceExpiryDate(item.insuranceExpiryDate());
            child.getLinkedFacilityCodes().clear();
            child.getLinkedFacilityCodes().addAll(defaultList(item.linkedFacilityCodes()));
            entity.getCollaterals().add(child);
        }
    }

    private void syncTboItems(CreditApplicationEntity entity, List<ApplicationApiModels.TboItem> items) {
        entity.getTboItems().clear();
        for (ApplicationApiModels.TboItem item : items) {
            TboItemEntity child = new TboItemEntity();
            child.setApplication(entity);
            child.setTboId(defaultString(item.tboId(), "TBO-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase()));
            child.setLinkedFacilityCode(item.linkedFacilityCode());
            child.setTboCategory(item.tboCategory());
            child.setDocumentRequirement(item.documentRequirement());
            child.setDueDate(item.dueDate());
            child.setStatus(defaultString(item.status(), "PENDING"));
            child.setPic(item.pic());
            entity.getTboItems().add(child);
        }
    }

    private void syncCovenants(CreditApplicationEntity entity, List<ApplicationApiModels.CovenantItem> items) {
        entity.getCovenants().clear();
        for (ApplicationApiModels.CovenantItem item : items) {
            CovenantItemEntity child = new CovenantItemEntity();
            child.setApplication(entity);
            child.setCovenantId(defaultString(item.covenantId(), "COV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase()));
            child.setCovenantType(item.covenantType());
            child.setDescriptionText(item.description());
            child.setTestingFrequency(item.testingFrequency());
            child.setMeasurementDate(item.measurementDate());
            child.setStatus(defaultString(item.status(), "PENDING"));
            child.setPenaltyDetails(item.penaltyDetails());
            entity.getCovenants().add(child);
        }
    }

    private void syncRuleHits(CreditApplicationEntity entity, List<RuleApiModels.RuleHit> hits) {
        entity.getRuleHits().clear();
        for (RuleApiModels.RuleHit hit : hits) {
            ApplicationRuleHitEntity child = new ApplicationRuleHitEntity();
            child.setApplication(entity);
            child.setRuleId(hit.ruleId());
            child.setRuleName(hit.ruleName());
            child.setRuleType(hit.ruleType());
            child.setMetricKey(hit.metricKey());
            child.setOperatorKey(hit.operator());
            child.setThresholdValue(hit.thresholdValue());
            child.setActualValue(hit.actualValue());
            child.setActionRouting(hit.actionRouting());
            child.setJustificationRequired(hit.justificationRequired());
            child.setMessageText(hit.message());
            entity.getRuleHits().add(child);
        }
    }

    private BigDecimal calculateProposedExposure(CreditApplicationEntity entity) {
        return entity.getFacilities().stream()
                .map(FacilityEntity::getLimitAmount)
                .map(this::safe)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal calculateAvailableLimit(BigDecimal coreCapitalBank, BigDecimal maxLimitPercentage, BigDecimal existingExposure, BigDecimal proposedExposure) {
        BigDecimal limit = safe(coreCapitalBank)
                .multiply(safe(maxLimitPercentage))
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        return limit.subtract(safe(existingExposure)).subtract(safe(proposedExposure));
    }

    private BigDecimal calculateCollateralCoverage(CreditApplicationEntity entity) {
        BigDecimal exposure = calculateProposedExposure(entity);
        if (exposure.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal bankable = entity.getCollaterals().stream()
                .map(CollateralEntity::getBankableValue)
                .map(this::safe)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return bankable.multiply(BigDecimal.valueOf(100)).divide(exposure, 4, RoundingMode.HALF_UP);
    }

    private BigDecimal resolveBankableValue(ApplicationApiModels.CollateralItem item) {
        if (item.bankableValue() != null) {
            return item.bankableValue();
        }
        BigDecimal liquidation = safe(item.liquidationValue());
        BigDecimal haircut = safe(item.marginOfAdvancePercentage()).divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
        return liquidation.multiply(BigDecimal.ONE.subtract(haircut)).max(BigDecimal.ZERO);
    }

    private ApplicationApiModels.CreditApplicationResponse toResponse(CreditApplicationEntity entity) {
        CustomerApiModels.CustomerSummaryResponse customer = new CustomerApiModels.CustomerSummaryResponse(
                entity.getCustomer().getCifNumber(),
                entity.getCustomer().getCompanyName(),
                entity.getCustomer().getCompanyType()
        );
        CustomerApiModels.QuantitativeAnalysisSnapshot latestAnalysis = customerPortfolioService.latestStandaloneAnalysis(entity.getCustomer().getCifNumber());

        return new ApplicationApiModels.CreditApplicationResponse(
                entity.getApplicationId(),
                entity.getApplicationDate(),
                entity.getApplicationType(),
                entity.getRmUsername(),
                entity.getBranchName(),
                entity.getRegionName(),
                entity.getCbcName(),
                entity.getGroupRelationshipStatus(),
                safe(entity.getCoreCapitalBank()),
                safe(entity.getMaxLendingLimitPercentage()),
                safe(entity.getExistingExposureGroup()),
                safe(entity.getProposedExposure()),
                safe(entity.getAvailableLimit()),
                entity.getBiSectorCode(),
                entity.getSubSectorDescription(),
                entity.getIndustryOutlook(),
                entity.getEsgGreenFinancingStatus(),
                entity.getJustificationNote(),
                entity.getWorkflowStatus(),
                entity.getCurrentApprovalTier(),
                entity.getProcessInstanceId(),
                customer,
                latestAnalysis,
                safe(entity.getCollateralCoverage()),
                entity.getFacilities().stream().map(this::toFacilityItem).toList(),
                entity.getCollaterals().stream().map(this::toCollateralItem).toList(),
                entity.getTboItems().stream().map(this::toTboItem).toList(),
                entity.getCovenants().stream().map(this::toCovenantItem).toList(),
                buildRuleEvaluation(entity),
                entity.getApprovalHistories().stream()
                        .sorted(Comparator.comparing(ApplicationApprovalHistoryEntity::getDecidedAt))
                        .map(history -> new WorkflowApiModels.ApprovalHistoryItem(history.getStage(), history.getDecision(), history.getActor(), history.getNotes(), history.getDecidedAt()))
                        .toList(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    private RuleApiModels.RuleEvaluationResponse buildRuleEvaluation(CreditApplicationEntity entity) {
        List<RuleApiModels.RuleHit> hits = entity.getRuleHits().stream()
                .map(hit -> new RuleApiModels.RuleHit(
                        hit.getRuleId(),
                        hit.getRuleName(),
                        hit.getRuleType(),
                        hit.getMetricKey(),
                        hit.getOperatorKey(),
                        hit.getThresholdValue(),
                        hit.getActualValue(),
                        hit.getActionRouting(),
                        hit.isJustificationRequired(),
                        hit.getMessageText()
                ))
                .toList();

        boolean hardStop = hits.stream().anyMatch(hit -> "HARD_STOP".equalsIgnoreCase(hit.ruleType()));
        boolean warningTriggered = hits.stream().anyMatch(hit -> "WARNING".equalsIgnoreCase(hit.ruleType()));
        boolean deviationTriggered = hits.stream().anyMatch(hit -> "DEVIATION".equalsIgnoreCase(hit.ruleType()));
        boolean justificationRequired = hits.stream().anyMatch(RuleApiModels.RuleHit::justificationRequired);
        String requiredTier = hardStop ? "HARD_STOP" : hits.stream()
                .map(RuleApiModels.RuleHit::actionRouting)
                .reduce("BRANCH_MANAGER", this::higherTier);

        return new RuleApiModels.RuleEvaluationResponse(
                hardStop,
                warningTriggered,
                deviationTriggered,
                justificationRequired,
                requiredTier,
                "BRANCH_MANAGER",
                "REGIONAL_HEAD",
                "CREDIT_COMMITTEE",
                "BOARD_OF_DIRECTORS",
                safe(entity.getProposedExposure()),
                safe(entity.getAvailableLimit()),
                safe(entity.getDebtToEquityRatio()),
                safe(entity.getCollateralCoverage()),
                safe(entity.getCurrentRatio()),
                hits
        );
    }

    private String higherTier(String current, String candidate) {
        return tierRank(candidate) > tierRank(current) ? candidate : current;
    }

    private int tierRank(String tier) {
        return switch (defaultString(tier)) {
            case "REGIONAL_HEAD" -> 2;
            case "CREDIT_COMMITTEE" -> 3;
            case "BOARD_OF_DIRECTORS" -> 4;
            case "BRANCH_MANAGER" -> 1;
            default -> 0;
        };
    }

    private ApplicationApiModels.FacilityItem toFacilityItem(FacilityEntity entity) {
        return new ApplicationApiModels.FacilityItem(
                entity.getFacilityCode(),
                entity.getFacilityName(),
                entity.getRevolvingStatus(),
                entity.getCurrencyCode(),
                entity.getLimitAmount(),
                entity.getTenorMonths(),
                entity.getMaturityDate(),
                entity.getInterestRateType(),
                entity.getInterestRate(),
                entity.getProvisionFee(),
                entity.getAdminFee(),
                entity.getCommitmentFee(),
                entity.getPenaltyFee(),
                entity.getRepaymentType(),
                entity.getPurposeOfLoan()
        );
    }

    private ApplicationApiModels.CollateralItem toCollateralItem(CollateralEntity entity) {
        return new ApplicationApiModels.CollateralItem(
                entity.getCollateralId(),
                entity.getCollateralType(),
                entity.getOwnerName(),
                entity.getLocationAddress(),
                entity.getAppraisalDate(),
                entity.getAppraiserName(),
                entity.getMarketValue(),
                entity.getLiquidationValue(),
                entity.getMarginOfAdvancePercentage(),
                entity.getBankableValue(),
                entity.getLegalDocumentInfo(),
                entity.getLegalDocumentExpiryDate(),
                entity.getInsuranceName(),
                entity.getInsuranceCoverageValue(),
                entity.getInsuranceExpiryDate(),
                new ArrayList<>(entity.getLinkedFacilityCodes())
        );
    }

    private ApplicationApiModels.TboItem toTboItem(TboItemEntity entity) {
        return new ApplicationApiModels.TboItem(
                entity.getTboId(),
                entity.getLinkedFacilityCode(),
                entity.getTboCategory(),
                entity.getDocumentRequirement(),
                entity.getDueDate(),
                entity.getStatus(),
                entity.getPic()
        );
    }

    private ApplicationApiModels.CovenantItem toCovenantItem(CovenantItemEntity entity) {
        return new ApplicationApiModels.CovenantItem(
                entity.getCovenantId(),
                entity.getCovenantType(),
                entity.getDescriptionText(),
                entity.getTestingFrequency(),
                entity.getMeasurementDate(),
                entity.getStatus(),
                entity.getPenaltyDetails()
        );
    }

    private AppUserEntity resolveCurrentUser() {
        try {
            return securityUtils.currentUserOptional()
                    .flatMap(user -> appUserRepository.findByUsername(user.getUsername()))
                    .orElse(null);
        } catch (AccessDeniedException ignored) {
            return null;
        }
    }

    private long countByStatus(List<CreditApplicationEntity> applications, String status) {
        return applications.stream().filter(application -> status.equalsIgnoreCase(application.getWorkflowStatus())).count();
    }

    private CreditApplicationEntity getEntity(String applicationId) {
        return creditApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Application not found: " + applicationId));
    }

    private void validateRequest(ApplicationApiModels.CreditApplicationRequest request) {
        if (request == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Application request is required");
        }
        if (isBlank(request.customerCif()) || isBlank(request.applicationType()) || isBlank(request.rmName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Customer CIF, application type, and RM name are required");
        }
    }

    private String generateApplicationId() {
        return "APP-" + LocalDate.now().toString().replace("-", "") + "-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private BigDecimal safe(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private String defaultString(String value) {
        return value == null ? "" : value.trim().toUpperCase();
    }

    private String defaultString(String value, String fallback) {
        return isBlank(value) ? fallback : value;
    }

    private <T> List<T> defaultList(List<T> values) {
        return values == null ? List.of() : values;
    }

    public record SubmissionContext(
            CreditApplicationEntity entity,
            CustomerApiModels.CustomerSummaryResponse customer,
            CustomerApiModels.QuantitativeAnalysisSnapshot latestAnalysis,
            RuleApiModels.RuleEvaluationResponse ruleEvaluation
    ) {
    }
}
