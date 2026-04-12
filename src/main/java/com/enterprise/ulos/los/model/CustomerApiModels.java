package com.enterprise.ulos.los.model;

import com.fasterxml.jackson.databind.JsonNode;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.List;

public final class CustomerApiModels {

    private CustomerApiModels() {
    }

    public record CustomerRequest(
            String cifNumber,
            String companyName,
            String legalName,
            String companyType,
            LocalDate registrationDate,
            String taxId,
            String sector,
            String location,
            String status,
            JsonNode profileData
    ) {
    }

    public record CustomerSummaryResponse(
            String cifNumber,
            String companyName,
            String sector,
            String location,
            String status,
            LocalDateTime updatedAt,
            String owningBranch,
            String businessSource
    ) {
        public CustomerSummaryResponse(String cifNumber, String companyName, String companyType) {
            this(cifNumber, companyName, companyType, null, "ACTIVE", null, null, null);
        }

        public CustomerSummaryResponse(
                String cifNumber,
                String companyName,
                String sector,
                String location,
                String status,
                LocalDateTime updatedAt
        ) {
            this(cifNumber, companyName, sector, location, status, updatedAt, null, null);
        }
    }

    public record CustomerSummaryPageResponse(
            java.util.List<CustomerSummaryResponse> items,
            long totalElements,
            int totalPages,
            int page,
            int size
    ) {
    }

    public record CustomerDetailResponse(
            Long id,
            String cifNumber,
            String companyName,
            String legalName,
            String companyType,
            LocalDate registrationDate,
            String taxId,
            String sector,
            String location,
            String status,
            JsonNode profileData,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
    }

    public record CustomerPortfolioRequest(
            String cifNumber,
            String companyName,
            String companyType,
            LocalDate dateOfEstablishment,
            String placeOfEstablishment,
            String taxId,
            String businessLicense,
            String officeAddress,
            String factoryAddress,
            List<KeyManagementItem> keyManagementItems,
            List<ShareholderItem> shareholders,
            List<RelatedPartyItem> relatedParties,
            List<FinancialStatementItem> standaloneStatements,
            List<FinancialStatementItem> consolidatedStatements,
            List<BankStatementItem> bankStatements,
            List<SupplierItem> suppliers,
            List<BuyerItem> buyers,
            List<CompetitorItem> competitors
    ) {
    }

    public record KeyManagementItem(
            String name,
            String nationalIdNumber,
            String title
    ) {
    }

    public record ShareholderItem(
            String name,
            BigDecimal ownershipPercentage,
            BigDecimal shareNominal
    ) {
    }

    public record RelatedPartyItem(
            String relationType,
            String name,
            String identityNumber,
            String contactDetails,
            String address
    ) {
    }

    public record FinancialStatementItem(
            String statementId,
            String period,
            String auditStatus,
            String auditorName,
            String groupHoldingName,
            BigDecimal cash,
            BigDecimal accountsReceivable,
            BigDecimal inventory,
            BigDecimal fixedAssets,
            BigDecimal accountsPayable,
            BigDecimal shortTermLoan,
            BigDecimal longTermLoan,
            BigDecimal totalEquity,
            BigDecimal salesRevenue,
            BigDecimal cogs,
            BigDecimal grossProfit,
            BigDecimal operatingExpenses,
            BigDecimal ebitda,
            BigDecimal interestExpense,
            BigDecimal netIncome,
            BigDecimal intercompanyElimination
    ) {
    }

    public record BankStatementItem(
            String bankName,
            String accountNumber,
            String period,
            BigDecimal totalInflow,
            BigDecimal totalOutflow,
            BigDecimal averageBalance,
            Integer chequeReturnCount,
            BigDecimal chequeReturnNominal
    ) {
    }

    public record SupplierItem(
            String supplierName,
            BigDecimal purchasePercentage,
            Integer paymentTermsDays
    ) {
    }

    public record BuyerItem(
            String buyerName,
            BigDecimal salesPercentage,
            Integer paymentTermsDays
    ) {
    }

    public record CompetitorItem(
            String competitorName,
            BigDecimal estimatedMarketShare
    ) {
    }

    public record QuantitativeAnalysisSnapshot(
            BigDecimal currentRatio,
            BigDecimal quickRatio,
            BigDecimal debtToEquityRatio,
            BigDecimal interestCoverage,
            BigDecimal returnOnAssets,
            BigDecimal returnOnEquity,
            BigDecimal grossMargin,
            BigDecimal netMargin,
            BigDecimal accountReceivableDays,
            BigDecimal inventoryDays,
            BigDecimal accountPayableDays,
            BigDecimal cashConversionCycle
    ) {
    }

    public record FinancialStatementAnalysis(
            String statementId,
            String period,
            String groupHoldingName,
            QuantitativeAnalysisSnapshot snapshot
    ) {
    }
}
