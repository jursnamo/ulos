package com.enterprise.ulos.los.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public final class CustomerApiModels {

    private CustomerApiModels() {
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
            List<KeyManagementItem> keyManagement,
            List<ShareholderItem> shareholders,
            List<RelatedPartyItem> relatedParties,
            List<FinancialStatementItem> financialStatements,
            List<FinancialStatementItem> consolidatedFinancialStatements,
            List<BankStatementItem> bankStatements,
            List<SupplierItem> suppliers,
            List<BuyerItem> buyers,
            List<CompetitorItem> competitors
    ) {
    }

    public record CustomerPortfolioResponse(
            String cifNumber,
            String companyName,
            String companyType,
            LocalDate dateOfEstablishment,
            String placeOfEstablishment,
            String taxId,
            String businessLicense,
            String officeAddress,
            String factoryAddress,
            List<KeyManagementItem> keyManagement,
            List<ShareholderItem> shareholders,
            List<RelatedPartyItem> relatedParties,
            List<FinancialStatementItem> financialStatements,
            List<FinancialStatementItem> consolidatedFinancialStatements,
            List<FinancialStatementAnalysis> quantitativeAnalyses,
            List<FinancialStatementAnalysis> consolidatedQuantitativeAnalyses,
            List<BankStatementItem> bankStatements,
            List<SupplierItem> suppliers,
            List<BuyerItem> buyers,
            List<CompetitorItem> competitors,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
    }

    public record CustomerSummaryResponse(
            String cifNumber,
            String companyName,
            String companyType
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

    public record FinancialStatementAnalysis(
            String statementId,
            String period,
            String groupHoldingName,
            QuantitativeAnalysisSnapshot analysis
    ) {
    }

    public record QuantitativeAnalysisSnapshot(
            BigDecimal currentRatio,
            BigDecimal quickRatio,
            BigDecimal debtToEquityRatio,
            BigDecimal interestCoverageRatio,
            BigDecimal returnOnAssets,
            BigDecimal returnOnEquity,
            BigDecimal grossProfitMargin,
            BigDecimal netProfitMargin,
            BigDecimal arDays,
            BigDecimal inventoryDays,
            BigDecimal apDays,
            BigDecimal cashConversionCycle
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
}
