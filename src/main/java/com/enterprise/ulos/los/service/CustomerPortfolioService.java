package com.enterprise.ulos.los.service;

import com.enterprise.ulos.los.entity.BankStatementEntity;
import com.enterprise.ulos.los.entity.BuyerProfileEntity;
import com.enterprise.ulos.los.entity.CompetitorProfileEntity;
import com.enterprise.ulos.los.entity.CustomerKeyManagementEntity;
import com.enterprise.ulos.los.entity.CustomerPortfolioEntity;
import com.enterprise.ulos.los.entity.CustomerRelatedPartyEntity;
import com.enterprise.ulos.los.entity.CustomerShareholderEntity;
import com.enterprise.ulos.los.entity.FinancialAnalysisSnapshotEntity;
import com.enterprise.ulos.los.entity.FinancialStatementEntity;
import com.enterprise.ulos.los.entity.SupplierProfileEntity;
import com.enterprise.ulos.los.model.CustomerApiModels;
import com.enterprise.ulos.los.repository.CustomerPortfolioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Comparator;
import java.util.List;

@Service
@Transactional
public class CustomerPortfolioService {

    private final CustomerPortfolioRepository customerPortfolioRepository;
    private final QuantitativeAnalysisService quantitativeAnalysisService;

    public CustomerPortfolioService(
            CustomerPortfolioRepository customerPortfolioRepository,
            QuantitativeAnalysisService quantitativeAnalysisService
    ) {
        this.customerPortfolioRepository = customerPortfolioRepository;
        this.quantitativeAnalysisService = quantitativeAnalysisService;
    }

    public CustomerApiModels.CustomerPortfolioResponse save(CustomerApiModels.CustomerPortfolioRequest request) {
        validateRequest(request);

        CustomerPortfolioEntity entity = customerPortfolioRepository.findById(request.cifNumber())
                .orElseGet(CustomerPortfolioEntity::new);

        entity.setCifNumber(request.cifNumber().trim());
        entity.setCompanyName(request.companyName().trim());
        entity.setCompanyType(request.companyType().trim());
        entity.setDateOfEstablishment(request.dateOfEstablishment());
        entity.setPlaceOfEstablishment(request.placeOfEstablishment());
        entity.setTaxId(request.taxId());
        entity.setBusinessLicense(request.businessLicense());
        entity.setOfficeAddress(request.officeAddress());
        entity.setFactoryAddress(request.factoryAddress());

        syncKeyManagement(entity, defaultList(request.keyManagement()));
        syncShareholders(entity, defaultList(request.shareholders()));
        syncRelatedParties(entity, defaultList(request.relatedParties()));
        syncFinancialStatements(entity, defaultList(request.financialStatements()), false);
        syncFinancialStatements(entity, defaultList(request.consolidatedFinancialStatements()), true);
        syncBankStatements(entity, defaultList(request.bankStatements()));
        syncSuppliers(entity, defaultList(request.suppliers()));
        syncBuyers(entity, defaultList(request.buyers()));
        syncCompetitors(entity, defaultList(request.competitors()));

        return toResponse(customerPortfolioRepository.save(entity));
    }

    @Transactional(readOnly = true)
    public List<CustomerApiModels.CustomerSummaryResponse> list() {
        return customerPortfolioRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(CustomerPortfolioEntity::getCreatedAt).reversed())
                .map(entity -> new CustomerApiModels.CustomerSummaryResponse(
                        entity.getCifNumber(),
                        entity.getCompanyName(),
                        entity.getCompanyType()
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public CustomerApiModels.CustomerPortfolioResponse get(String cifNumber) {
        return toResponse(getEntity(cifNumber));
    }

    @Transactional(readOnly = true)
    public CustomerApiModels.CustomerSummaryResponse getSummary(String cifNumber) {
        CustomerPortfolioEntity entity = getEntity(cifNumber);
        return new CustomerApiModels.CustomerSummaryResponse(entity.getCifNumber(), entity.getCompanyName(), entity.getCompanyType());
    }

    @Transactional(readOnly = true)
    public CustomerApiModels.QuantitativeAnalysisSnapshot latestStandaloneAnalysis(String cifNumber) {
        CustomerPortfolioEntity entity = getEntity(cifNumber);
        return entity.getFinancialStatements().stream()
                .filter(statement -> !statement.isConsolidated())
                .sorted(Comparator.comparing(FinancialStatementEntity::getPeriodLabel, Comparator.nullsLast(String::compareTo)).reversed())
                .map(FinancialStatementEntity::getAnalysisSnapshot)
                .filter(snapshot -> snapshot != null)
                .map(this::toSnapshot)
                .findFirst()
                .orElse(zeroSnapshot());
    }

    private void syncKeyManagement(CustomerPortfolioEntity entity, List<CustomerApiModels.KeyManagementItem> items) {
        entity.getKeyManagementItems().clear();
        for (CustomerApiModels.KeyManagementItem item : items) {
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
        for (CustomerApiModels.ShareholderItem item : items) {
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
        for (CustomerApiModels.RelatedPartyItem item : items) {
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
        entity.getFinancialStatements().removeIf(statement -> statement.isConsolidated() == consolidated);
        for (CustomerApiModels.FinancialStatementItem item : items) {
            FinancialStatementEntity statement = new FinancialStatementEntity();
            statement.setCustomer(entity);
            statement.setStatementId(item.statementId());
            statement.setPeriodLabel(item.period());
            statement.setAuditStatus(item.auditStatus());
            statement.setAuditorName(item.auditorName());
            statement.setGroupHoldingName(item.groupHoldingName());
            statement.setConsolidated(consolidated);
            statement.setCash(item.cash());
            statement.setAccountsReceivable(item.accountsReceivable());
            statement.setInventory(item.inventory());
            statement.setFixedAssets(item.fixedAssets());
            statement.setAccountsPayable(item.accountsPayable());
            statement.setShortTermLoan(item.shortTermLoan());
            statement.setLongTermLoan(item.longTermLoan());
            statement.setTotalEquity(item.totalEquity());
            statement.setSalesRevenue(item.salesRevenue());
            statement.setCogs(item.cogs());
            statement.setGrossProfit(item.grossProfit());
            statement.setOperatingExpenses(item.operatingExpenses());
            statement.setEbitda(item.ebitda());
            statement.setInterestExpense(item.interestExpense());
            statement.setNetIncome(item.netIncome());
            statement.setIntercompanyElimination(item.intercompanyElimination());

            CustomerApiModels.QuantitativeAnalysisSnapshot analysis = quantitativeAnalysisService.analyze(item);
            FinancialAnalysisSnapshotEntity snapshot = new FinancialAnalysisSnapshotEntity();
            snapshot.setFinancialStatement(statement);
            snapshot.setCurrentRatio(analysis.currentRatio());
            snapshot.setQuickRatio(analysis.quickRatio());
            snapshot.setDebtToEquityRatio(analysis.debtToEquityRatio());
            snapshot.setInterestCoverageRatio(analysis.interestCoverageRatio());
            snapshot.setReturnOnAssets(analysis.returnOnAssets());
            snapshot.setReturnOnEquity(analysis.returnOnEquity());
            snapshot.setGrossProfitMargin(analysis.grossProfitMargin());
            snapshot.setNetProfitMargin(analysis.netProfitMargin());
            snapshot.setArDays(analysis.arDays());
            snapshot.setInventoryDays(analysis.inventoryDays());
            snapshot.setApDays(analysis.apDays());
            snapshot.setCashConversionCycle(analysis.cashConversionCycle());
            statement.setAnalysisSnapshot(snapshot);

            entity.getFinancialStatements().add(statement);
        }
    }

    private void syncBankStatements(CustomerPortfolioEntity entity, List<CustomerApiModels.BankStatementItem> items) {
        entity.getBankStatements().clear();
        for (CustomerApiModels.BankStatementItem item : items) {
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
        for (CustomerApiModels.SupplierItem item : items) {
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
        for (CustomerApiModels.BuyerItem item : items) {
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
        for (CustomerApiModels.CompetitorItem item : items) {
            CompetitorProfileEntity child = new CompetitorProfileEntity();
            child.setCustomer(entity);
            child.setCompetitorName(item.competitorName());
            child.setEstimatedMarketShare(item.estimatedMarketShare());
            entity.getCompetitors().add(child);
        }
    }

    private CustomerApiModels.CustomerPortfolioResponse toResponse(CustomerPortfolioEntity entity) {
        List<FinancialStatementEntity> standalone = entity.getFinancialStatements().stream()
                .filter(statement -> !statement.isConsolidated())
                .sorted(Comparator.comparing(FinancialStatementEntity::getPeriodLabel, Comparator.nullsLast(String::compareTo)).reversed())
                .toList();
        List<FinancialStatementEntity> consolidated = entity.getFinancialStatements().stream()
                .filter(FinancialStatementEntity::isConsolidated)
                .sorted(Comparator.comparing(FinancialStatementEntity::getPeriodLabel, Comparator.nullsLast(String::compareTo)).reversed())
                .toList();

        return new CustomerApiModels.CustomerPortfolioResponse(
                entity.getCifNumber(),
                entity.getCompanyName(),
                entity.getCompanyType(),
                entity.getDateOfEstablishment(),
                entity.getPlaceOfEstablishment(),
                entity.getTaxId(),
                entity.getBusinessLicense(),
                entity.getOfficeAddress(),
                entity.getFactoryAddress(),
                entity.getKeyManagementItems().stream()
                        .map(item -> new CustomerApiModels.KeyManagementItem(item.getName(), item.getNationalIdNumber(), item.getTitle()))
                        .toList(),
                entity.getShareholders().stream()
                        .map(item -> new CustomerApiModels.ShareholderItem(item.getName(), item.getOwnershipPercentage(), item.getShareNominal()))
                        .toList(),
                entity.getRelatedParties().stream()
                        .map(item -> new CustomerApiModels.RelatedPartyItem(item.getRelationType(), item.getName(), item.getIdentityNumber(), item.getContactDetails(), item.getAddress()))
                        .toList(),
                standalone.stream().map(this::toFinancialStatementItem).toList(),
                consolidated.stream().map(this::toFinancialStatementItem).toList(),
                standalone.stream().map(this::toFinancialAnalysis).toList(),
                consolidated.stream().map(this::toFinancialAnalysis).toList(),
                entity.getBankStatements().stream()
                        .map(item -> new CustomerApiModels.BankStatementItem(
                                item.getBankName(),
                                item.getAccountNumber(),
                                item.getPeriodLabel(),
                                item.getTotalInflow(),
                                item.getTotalOutflow(),
                                item.getAverageBalance(),
                                item.getChequeReturnCount(),
                                item.getChequeReturnNominal()
                        ))
                        .toList(),
                entity.getSuppliers().stream()
                        .map(item -> new CustomerApiModels.SupplierItem(item.getSupplierName(), item.getPurchasePercentage(), item.getPaymentTermsDays()))
                        .toList(),
                entity.getBuyers().stream()
                        .map(item -> new CustomerApiModels.BuyerItem(item.getBuyerName(), item.getSalesPercentage(), item.getPaymentTermsDays()))
                        .toList(),
                entity.getCompetitors().stream()
                        .map(item -> new CustomerApiModels.CompetitorItem(item.getCompetitorName(), item.getEstimatedMarketShare()))
                        .toList(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    private CustomerApiModels.FinancialStatementItem toFinancialStatementItem(FinancialStatementEntity entity) {
        return new CustomerApiModels.FinancialStatementItem(
                entity.getStatementId(),
                entity.getPeriodLabel(),
                entity.getAuditStatus(),
                entity.getAuditorName(),
                entity.getGroupHoldingName(),
                entity.getCash(),
                entity.getAccountsReceivable(),
                entity.getInventory(),
                entity.getFixedAssets(),
                entity.getAccountsPayable(),
                entity.getShortTermLoan(),
                entity.getLongTermLoan(),
                entity.getTotalEquity(),
                entity.getSalesRevenue(),
                entity.getCogs(),
                entity.getGrossProfit(),
                entity.getOperatingExpenses(),
                entity.getEbitda(),
                entity.getInterestExpense(),
                entity.getNetIncome(),
                entity.getIntercompanyElimination()
        );
    }

    private CustomerApiModels.FinancialStatementAnalysis toFinancialAnalysis(FinancialStatementEntity entity) {
        return new CustomerApiModels.FinancialStatementAnalysis(
                entity.getStatementId(),
                entity.getPeriodLabel(),
                entity.getGroupHoldingName(),
                entity.getAnalysisSnapshot() == null ? zeroSnapshot() : toSnapshot(entity.getAnalysisSnapshot())
        );
    }

    private CustomerApiModels.QuantitativeAnalysisSnapshot toSnapshot(FinancialAnalysisSnapshotEntity entity) {
        return new CustomerApiModels.QuantitativeAnalysisSnapshot(
                entity.getCurrentRatio(),
                entity.getQuickRatio(),
                entity.getDebtToEquityRatio(),
                entity.getInterestCoverageRatio(),
                entity.getReturnOnAssets(),
                entity.getReturnOnEquity(),
                entity.getGrossProfitMargin(),
                entity.getNetProfitMargin(),
                entity.getArDays(),
                entity.getInventoryDays(),
                entity.getApDays(),
                entity.getCashConversionCycle()
        );
    }

    private CustomerApiModels.QuantitativeAnalysisSnapshot zeroSnapshot() {
        return new CustomerApiModels.QuantitativeAnalysisSnapshot(
                java.math.BigDecimal.ZERO,
                java.math.BigDecimal.ZERO,
                java.math.BigDecimal.ZERO,
                java.math.BigDecimal.ZERO,
                java.math.BigDecimal.ZERO,
                java.math.BigDecimal.ZERO,
                java.math.BigDecimal.ZERO,
                java.math.BigDecimal.ZERO,
                java.math.BigDecimal.ZERO,
                java.math.BigDecimal.ZERO,
                java.math.BigDecimal.ZERO,
                java.math.BigDecimal.ZERO
        );
    }

    private CustomerPortfolioEntity getEntity(String cifNumber) {
        return customerPortfolioRepository.findById(cifNumber)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found: " + cifNumber));
    }

    private void validateRequest(CustomerApiModels.CustomerPortfolioRequest request) {
        if (request == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Customer request is required");
        }
        if (isBlank(request.cifNumber()) || isBlank(request.companyName()) || isBlank(request.companyType())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CIF number, company name, and company type are required");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private <T> List<T> defaultList(List<T> values) {
        return values == null ? List.of() : values;
    }
}
