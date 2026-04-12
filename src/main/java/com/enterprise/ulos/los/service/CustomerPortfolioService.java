package com.enterprise.ulos.los.service;

import com.enterprise.ulos.los.entity.CustomerEntity;
import com.enterprise.ulos.los.entity.CustomerPortfolioEntity;
import com.enterprise.ulos.los.entity.CustomerKeyManagementEntity;
import com.enterprise.ulos.los.entity.CustomerShareholderEntity;
import com.enterprise.ulos.los.entity.CustomerRelatedPartyEntity;
import com.enterprise.ulos.los.entity.FinancialStatementEntity;
import com.enterprise.ulos.los.entity.BankStatementEntity;
import com.enterprise.ulos.los.entity.SupplierProfileEntity;
import com.enterprise.ulos.los.entity.BuyerProfileEntity;
import com.enterprise.ulos.los.entity.CompetitorProfileEntity;
import com.enterprise.ulos.los.model.CustomerApiModels;
import com.enterprise.ulos.los.repository.CustomerRepository;
import com.enterprise.ulos.los.repository.CustomerPortfolioRepository;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class CustomerPortfolioService {

    private final CustomerRepository customerRepository;
    private final CustomerPortfolioRepository customerPortfolioRepository;
    private final JsonSectionMapper jsonSectionMapper;
    private final QuantitativeAnalysisService quantitativeAnalysisService;

    public CustomerPortfolioService(
            CustomerRepository customerRepository,
            CustomerPortfolioRepository customerPortfolioRepository,
            JsonSectionMapper jsonSectionMapper,
            QuantitativeAnalysisService quantitativeAnalysisService
    ) {
        this.customerRepository = customerRepository;
        this.customerPortfolioRepository = customerPortfolioRepository;
        this.jsonSectionMapper = jsonSectionMapper;
        this.quantitativeAnalysisService = quantitativeAnalysisService;
    }

    public CustomerApiModels.CustomerDetailResponse save(CustomerApiModels.CustomerRequest request) {
        validateRequest(request);

        String cif = request.cifNumber() == null || request.cifNumber().isBlank()
                ? generateCif()
                : request.cifNumber().trim();

        CustomerEntity entity = customerRepository.findByCifNumber(cif).orElseGet(CustomerEntity::new);
        entity.setCifNumber(cif);
        entity.setCompanyName(request.companyName().trim());
        entity.setLegalName(request.legalName());
        entity.setCompanyType(defaultValue(request.companyType(), "Corporate"));
        entity.setRegistrationDate(request.registrationDate() == null ? LocalDate.now() : request.registrationDate());
        entity.setTaxId(request.taxId());
        entity.setSector(request.sector());
        entity.setLocation(request.location());
        entity.setStatus(defaultValue(request.status(), "ACTIVE"));
        entity.setProfileJson(jsonSectionMapper.toJson(request.profileData()));

        return toDetail(customerRepository.save(entity));
    }

    public CustomerApiModels.CustomerDetailResponse save(CustomerApiModels.CustomerPortfolioRequest request) {
        validatePortfolioRequest(request);

        String cif = request.cifNumber() == null || request.cifNumber().isBlank()
                ? generateCif()
                : request.cifNumber().trim();

        CustomerPortfolioEntity legacy = customerPortfolioRepository.findById(cif).orElseGet(CustomerPortfolioEntity::new);
        legacy.setCifNumber(cif);
        legacy.setCompanyName(request.companyName().trim());
        legacy.setCompanyType(defaultValue(request.companyType(), "Corporate"));
        legacy.setDateOfEstablishment(request.dateOfEstablishment() == null ? LocalDate.now() : request.dateOfEstablishment());
        legacy.setPlaceOfEstablishment(request.placeOfEstablishment());
        legacy.setTaxId(request.taxId());
        legacy.setBusinessLicense(request.businessLicense());
        legacy.setOfficeAddress(request.officeAddress());
        legacy.setFactoryAddress(request.factoryAddress());

        syncKeyManagement(legacy, request.keyManagementItems());
        syncShareholders(legacy, request.shareholders());
        syncRelatedParties(legacy, request.relatedParties());
        syncFinancialStatements(legacy, request.standaloneStatements(), false);
        syncFinancialStatements(legacy, request.consolidatedStatements(), true);
        syncBankStatements(legacy, request.bankStatements());
        syncSuppliers(legacy, request.suppliers());
        syncBuyers(legacy, request.buyers());
        syncCompetitors(legacy, request.competitors());
        customerPortfolioRepository.save(legacy);

        // Keep workspace customer table synced for newer portfolio modules.
        save(new CustomerApiModels.CustomerRequest(
                cif,
                request.companyName(),
                request.companyName(),
                request.companyType(),
                request.dateOfEstablishment(),
                request.taxId(),
                null,
                request.placeOfEstablishment(),
                "ACTIVE",
                null
        ));

        return getByCif(cif);
    }

    @Transactional(readOnly = true)
    public List<CustomerApiModels.CustomerSummaryResponse> list() {
        return customerRepository.findAll().stream()
                .sorted(Comparator.comparing(CustomerEntity::getUpdatedAt).reversed())
                .map(this::toSummary)
                .toList();
    }

    @Transactional(readOnly = true)
    public CustomerApiModels.CustomerSummaryPageResponse listPage(String keyword, int page, int size) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.max(1, Math.min(size, 100));
        String normalizedKeyword = keyword == null ? null : keyword.trim();
        if (normalizedKeyword != null && normalizedKeyword.isBlank()) {
            normalizedKeyword = null;
        }

        Page<CustomerEntity> result = customerRepository.search(
                normalizedKeyword,
                PageRequest.of(safePage, safeSize, Sort.by(Sort.Direction.DESC, "updatedAt"))
        );

        List<CustomerApiModels.CustomerSummaryResponse> items = result.getContent().stream()
                .map(this::toSummary)
                .toList();

        return new CustomerApiModels.CustomerSummaryPageResponse(
                items,
                result.getTotalElements(),
                result.getTotalPages(),
                result.getNumber(),
                result.getSize()
        );
    }

    @Transactional(readOnly = true)
    public CustomerApiModels.CustomerDetailResponse getByCif(String cifNumber) {
        return toDetail(findEntityByCif(cifNumber));
    }

    @Transactional(readOnly = true)
    public CustomerApiModels.CustomerSummaryResponse getSummary(String cifNumber) {
        return customerPortfolioRepository.findById(cifNumber)
                .map(item -> new CustomerApiModels.CustomerSummaryResponse(item.getCifNumber(), item.getCompanyName(), item.getCompanyType()))
                .orElseGet(() -> {
                    CustomerEntity entity = findEntityByCif(cifNumber);
                    return new CustomerApiModels.CustomerSummaryResponse(
                            entity.getCifNumber(),
                            entity.getCompanyName(),
                            entity.getCompanyType(),
                            entity.getLocation(),
                            entity.getStatus(),
                            entity.getUpdatedAt(),
                            extractProfileFormField(entity, "owning_branch", "owningBranch"),
                            extractProfileFormField(entity, "business_source", "businessSource")
                    );
                });
    }

    @Transactional(readOnly = true)
    public CustomerApiModels.QuantitativeAnalysisSnapshot latestStandaloneAnalysis(String cifNumber) {
        return customerPortfolioRepository.findById(cifNumber)
                .map(CustomerPortfolioEntity::getFinancialStatements)
                .map(items -> items.stream()
                        .filter(item -> !item.isConsolidated())
                        .map(this::toFinancialStatementItem)
                        .toList())
                .filter(items -> !items.isEmpty())
                .map(quantitativeAnalysisService::latestAnalysis)
                .orElseGet(this::zeroSnapshot);
    }

    @Transactional(readOnly = true)
    public CustomerEntity findEntityByCif(String cifNumber) {
        return customerRepository.findByCifNumber(cifNumber)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found: " + cifNumber));
    }

    private CustomerApiModels.CustomerDetailResponse toDetail(CustomerEntity entity) {
        return new CustomerApiModels.CustomerDetailResponse(
                entity.getId(),
                entity.getCifNumber(),
                entity.getCompanyName(),
                entity.getLegalName(),
                entity.getCompanyType(),
                entity.getRegistrationDate(),
                entity.getTaxId(),
                entity.getSector(),
                entity.getLocation(),
                entity.getStatus(),
                jsonSectionMapper.fromJson(entity.getProfileJson()),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    private void validateRequest(CustomerApiModels.CustomerRequest request) {
        if (request == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Customer payload is required");
        }
        if (request.companyName() == null || request.companyName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "companyName is required");
        }
    }

    private void validatePortfolioRequest(CustomerApiModels.CustomerPortfolioRequest request) {
        if (request == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Customer portfolio payload is required");
        }
        if (request.companyName() == null || request.companyName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "companyName is required");
        }
    }

    private CustomerApiModels.CustomerSummaryResponse toSummary(CustomerEntity entity) {
        return new CustomerApiModels.CustomerSummaryResponse(
                entity.getCifNumber(),
                entity.getCompanyName(),
                entity.getSector(),
                entity.getLocation(),
                entity.getStatus(),
                entity.getUpdatedAt(),
                extractProfileFormField(entity, "owning_branch", "owningBranch"),
                extractProfileFormField(entity, "business_source", "businessSource")
        );
    }

    private String extractProfileFormField(CustomerEntity entity, String... fieldKeys) {
        if (entity == null || fieldKeys == null || fieldKeys.length == 0) {
            return null;
        }
        JsonNode profile = jsonSectionMapper.fromJson(entity.getProfileJson());
        JsonNode formFields = profile.path("formFields");
        if (formFields.isMissingNode() || formFields.isNull()) {
            return null;
        }

        for (String fieldKey : fieldKeys) {
            if (fieldKey == null || fieldKey.isBlank()) {
                continue;
            }
            JsonNode valueNode = formFields.path(fieldKey);
            String resolved = extractTextValue(valueNode);
            if (resolved != null && !resolved.isBlank()) {
                return resolved;
            }
        }
        return null;
    }

    private String extractTextValue(JsonNode valueNode) {
        if (valueNode == null || valueNode.isMissingNode() || valueNode.isNull()) {
            return null;
        }
        if (valueNode.isArray()) {
            for (JsonNode item : valueNode) {
                String value = extractTextValue(item);
                if (value != null && !value.isBlank()) {
                    return value;
                }
            }
            return null;
        }
        if (valueNode.isObject()) {
            String fromLabel = valueNode.path("label").asText("").trim();
            if (!fromLabel.isBlank()) {
                return fromLabel;
            }
            String fromValue = valueNode.path("value").asText("").trim();
            if (!fromValue.isBlank()) {
                return fromValue;
            }
            return null;
        }
        String value = valueNode.asText("").trim();
        return value.isBlank() ? null : value;
    }

    private String generateCif() {
        return "DEB-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private String defaultValue(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value.trim();
    }

    private void syncKeyManagement(CustomerPortfolioEntity entity, List<CustomerApiModels.KeyManagementItem> items) {
        entity.getKeyManagementItems().clear();
        for (CustomerApiModels.KeyManagementItem item : safeList(items)) {
            CustomerKeyManagementEntity child = new CustomerKeyManagementEntity();
            child.setCustomer(entity);
            child.setName(item.name());
            child.setNationalIdNumber(item.nationalIdNumber());
            child.setTitle(item.title());
            entity.getKeyManagementItems().add(child);
        }
    }

    private void syncShareholders(CustomerPortfolioEntity entity, List<CustomerApiModels.ShareholderItem> items) {
        entity.getShareholders().clear();
        for (CustomerApiModels.ShareholderItem item : safeList(items)) {
            CustomerShareholderEntity child = new CustomerShareholderEntity();
            child.setCustomer(entity);
            child.setName(item.name());
            child.setOwnershipPercentage(item.ownershipPercentage());
            child.setShareNominal(item.shareNominal());
            entity.getShareholders().add(child);
        }
    }

    private void syncRelatedParties(CustomerPortfolioEntity entity, List<CustomerApiModels.RelatedPartyItem> items) {
        entity.getRelatedParties().clear();
        for (CustomerApiModels.RelatedPartyItem item : safeList(items)) {
            CustomerRelatedPartyEntity child = new CustomerRelatedPartyEntity();
            child.setCustomer(entity);
            child.setRelationType(item.relationType());
            child.setName(item.name());
            child.setIdentityNumber(item.identityNumber());
            child.setContactDetails(item.contactDetails());
            child.setAddress(item.address());
            entity.getRelatedParties().add(child);
        }
    }

    private void syncFinancialStatements(
            CustomerPortfolioEntity entity,
            List<CustomerApiModels.FinancialStatementItem> items,
            boolean consolidated
    ) {
        if (!consolidated) {
            entity.getFinancialStatements().clear();
        }
        for (CustomerApiModels.FinancialStatementItem item : safeList(items)) {
            FinancialStatementEntity child = new FinancialStatementEntity();
            child.setCustomer(entity);
            child.setStatementId(item.statementId());
            child.setPeriodLabel(item.period());
            child.setAuditStatus(item.auditStatus());
            child.setAuditorName(item.auditorName());
            child.setGroupHoldingName(item.groupHoldingName());
            child.setConsolidated(consolidated);
            child.setCash(item.cash());
            child.setAccountsReceivable(item.accountsReceivable());
            child.setInventory(item.inventory());
            child.setFixedAssets(item.fixedAssets());
            child.setAccountsPayable(item.accountsPayable());
            child.setShortTermLoan(item.shortTermLoan());
            child.setLongTermLoan(item.longTermLoan());
            child.setTotalEquity(item.totalEquity());
            child.setSalesRevenue(item.salesRevenue());
            child.setCogs(item.cogs());
            child.setGrossProfit(item.grossProfit());
            child.setOperatingExpenses(item.operatingExpenses());
            child.setEbitda(item.ebitda());
            child.setInterestExpense(item.interestExpense());
            child.setNetIncome(item.netIncome());
            child.setIntercompanyElimination(item.intercompanyElimination());
            entity.getFinancialStatements().add(child);
        }
    }

    private void syncBankStatements(CustomerPortfolioEntity entity, List<CustomerApiModels.BankStatementItem> items) {
        entity.getBankStatements().clear();
        for (CustomerApiModels.BankStatementItem item : safeList(items)) {
            BankStatementEntity child = new BankStatementEntity();
            child.setCustomer(entity);
            child.setBankName(item.bankName());
            child.setAccountNumber(item.accountNumber());
            child.setPeriodLabel(item.period());
            child.setTotalInflow(item.totalInflow());
            child.setTotalOutflow(item.totalOutflow());
            child.setAverageBalance(item.averageBalance());
            child.setChequeReturnCount(item.chequeReturnCount());
            child.setChequeReturnNominal(item.chequeReturnNominal());
            entity.getBankStatements().add(child);
        }
    }

    private void syncSuppliers(CustomerPortfolioEntity entity, List<CustomerApiModels.SupplierItem> items) {
        entity.getSuppliers().clear();
        for (CustomerApiModels.SupplierItem item : safeList(items)) {
            SupplierProfileEntity child = new SupplierProfileEntity();
            child.setCustomer(entity);
            child.setSupplierName(item.supplierName());
            child.setPurchasePercentage(item.purchasePercentage());
            child.setPaymentTermsDays(item.paymentTermsDays());
            entity.getSuppliers().add(child);
        }
    }

    private void syncBuyers(CustomerPortfolioEntity entity, List<CustomerApiModels.BuyerItem> items) {
        entity.getBuyers().clear();
        for (CustomerApiModels.BuyerItem item : safeList(items)) {
            BuyerProfileEntity child = new BuyerProfileEntity();
            child.setCustomer(entity);
            child.setBuyerName(item.buyerName());
            child.setSalesPercentage(item.salesPercentage());
            child.setPaymentTermsDays(item.paymentTermsDays());
            entity.getBuyers().add(child);
        }
    }

    private void syncCompetitors(CustomerPortfolioEntity entity, List<CustomerApiModels.CompetitorItem> items) {
        entity.getCompetitors().clear();
        for (CustomerApiModels.CompetitorItem item : safeList(items)) {
            CompetitorProfileEntity child = new CompetitorProfileEntity();
            child.setCustomer(entity);
            child.setCompetitorName(item.competitorName());
            child.setEstimatedMarketShare(item.estimatedMarketShare());
            entity.getCompetitors().add(child);
        }
    }

    private CustomerApiModels.FinancialStatementItem toFinancialStatementItem(FinancialStatementEntity item) {
        return new CustomerApiModels.FinancialStatementItem(
                item.getStatementId(),
                item.getPeriodLabel(),
                item.getAuditStatus(),
                item.getAuditorName(),
                item.getGroupHoldingName(),
                item.getCash(),
                item.getAccountsReceivable(),
                item.getInventory(),
                item.getFixedAssets(),
                item.getAccountsPayable(),
                item.getShortTermLoan(),
                item.getLongTermLoan(),
                item.getTotalEquity(),
                item.getSalesRevenue(),
                item.getCogs(),
                item.getGrossProfit(),
                item.getOperatingExpenses(),
                item.getEbitda(),
                item.getInterestExpense(),
                item.getNetIncome(),
                item.getIntercompanyElimination()
        );
    }

    private CustomerApiModels.QuantitativeAnalysisSnapshot zeroSnapshot() {
        return new CustomerApiModels.QuantitativeAnalysisSnapshot(
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO
        );
    }

    private <T> List<T> safeList(List<T> values) {
        return values == null ? List.of() : values;
    }
}
