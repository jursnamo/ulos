package com.enterprise.ulos.los.service;

import com.enterprise.ulos.los.model.CustomerApiModels;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class QuantitativeAnalysisService {

    private static final BigDecimal DAYS_IN_YEAR = BigDecimal.valueOf(365);
    private static final int SCALE = 4;

    public List<CustomerApiModels.FinancialStatementAnalysis> buildAnalyses(List<CustomerApiModels.FinancialStatementItem> statements) {
        return statements.stream()
                .map(statement -> new CustomerApiModels.FinancialStatementAnalysis(
                        statement.statementId(),
                        statement.period(),
                        statement.groupHoldingName(),
                        analyze(statement)
                ))
                .toList();
    }

    public CustomerApiModels.QuantitativeAnalysisSnapshot analyze(CustomerApiModels.FinancialStatementItem statement) {
        BigDecimal cash = nvl(statement.cash());
        BigDecimal receivables = nvl(statement.accountsReceivable());
        BigDecimal inventory = nvl(statement.inventory());
        BigDecimal fixedAssets = nvl(statement.fixedAssets());
        BigDecimal accountsPayable = nvl(statement.accountsPayable());
        BigDecimal shortTermLoan = nvl(statement.shortTermLoan());
        BigDecimal longTermLoan = nvl(statement.longTermLoan());
        BigDecimal totalEquity = nvl(statement.totalEquity());
        BigDecimal salesRevenue = nvl(statement.salesRevenue());
        BigDecimal cogs = nvl(statement.cogs());
        BigDecimal grossProfit = statement.grossProfit() == null
                ? salesRevenue.subtract(cogs)
                : statement.grossProfit();
        BigDecimal ebitda = nvl(statement.ebitda());
        BigDecimal interestExpense = nvl(statement.interestExpense());
        BigDecimal netIncome = nvl(statement.netIncome());
        BigDecimal intercompanyElimination = nvl(statement.intercompanyElimination());

        BigDecimal currentAssets = cash.add(receivables).add(inventory);
        BigDecimal currentLiabilities = accountsPayable.add(shortTermLoan);
        BigDecimal totalDebt = shortTermLoan.add(longTermLoan);
        BigDecimal totalAssets = currentAssets.add(fixedAssets).subtract(intercompanyElimination.max(BigDecimal.ZERO));

        BigDecimal currentRatio = ratio(currentAssets, currentLiabilities);
        BigDecimal quickRatio = ratio(cash.add(receivables), currentLiabilities);
        BigDecimal debtToEquity = ratio(totalDebt, totalEquity);
        BigDecimal interestCoverage = ratio(ebitda, interestExpense);
        BigDecimal roa = ratio(netIncome, totalAssets);
        BigDecimal roe = ratio(netIncome, totalEquity);
        BigDecimal grossMargin = ratio(grossProfit, salesRevenue);
        BigDecimal netMargin = ratio(netIncome, salesRevenue);
        BigDecimal arDays = ratio(receivables.multiply(DAYS_IN_YEAR), salesRevenue);
        BigDecimal inventoryDays = ratio(inventory.multiply(DAYS_IN_YEAR), cogs);
        BigDecimal apDays = ratio(accountsPayable.multiply(DAYS_IN_YEAR), cogs);
        BigDecimal cashConversionCycle = arDays.add(inventoryDays).subtract(apDays);

        return new CustomerApiModels.QuantitativeAnalysisSnapshot(
                currentRatio,
                quickRatio,
                debtToEquity,
                interestCoverage,
                roa,
                roe,
                grossMargin,
                netMargin,
                arDays,
                inventoryDays,
                apDays,
                cashConversionCycle
        );
    }

    public CustomerApiModels.QuantitativeAnalysisSnapshot latestAnalysis(List<CustomerApiModels.FinancialStatementItem> statements) {
        return latestStatement(statements)
                .map(this::analyze)
                .orElse(new CustomerApiModels.QuantitativeAnalysisSnapshot(
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
                ));
    }

    private Optional<CustomerApiModels.FinancialStatementItem> latestStatement(List<CustomerApiModels.FinancialStatementItem> statements) {
        return statements.stream()
                .max(Comparator.comparing(statement -> statement.period() == null ? "" : statement.period()));
    }

    private BigDecimal ratio(BigDecimal numerator, BigDecimal denominator) {
        if (denominator == null || denominator.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return numerator.divide(denominator, SCALE, RoundingMode.HALF_UP);
    }

    private BigDecimal nvl(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }
}
