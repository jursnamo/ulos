package com.enterprise.ulos.los.config;

import com.enterprise.ulos.domain.bpmn.BpmnModelEntity;
import com.enterprise.ulos.los.model.ApplicationApiModels;
import com.enterprise.ulos.los.model.AuthApiModels;
import com.enterprise.ulos.los.model.CustomerApiModels;
import com.enterprise.ulos.los.repository.AppUserRepository;
import com.enterprise.ulos.los.repository.CreditApplicationRepository;
import com.enterprise.ulos.los.repository.CustomerPortfolioRepository;
import com.enterprise.ulos.repository.BpmnModelRepository;
import com.enterprise.ulos.los.service.CorporateApprovalWorkflowService;
import com.enterprise.ulos.los.service.CreditApplicationService;
import com.enterprise.ulos.los.service.CustomerPortfolioService;
import com.enterprise.ulos.los.service.UserManagementService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.repository.ProcessDefinition;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

@Configuration
public class LosDemoDataInitializer {

    private static final String SAMPLE_CIF = "CIF-ENT-001";
    private static final String SAMPLE_APPLICATION_ID = "APP-ENT-001";

    @Bean
    CommandLineRunner seedDemoData(
            UserManagementService userManagementService,
            AppUserRepository appUserRepository,
            CustomerPortfolioRepository customerPortfolioRepository,
            CreditApplicationRepository creditApplicationRepository,
            CustomerPortfolioService customerPortfolioService,
            CreditApplicationService creditApplicationService,
            CorporateApprovalWorkflowService corporateApprovalWorkflowService,
            BpmnModelRepository bpmnModelRepository,
            RepositoryService repositoryService
    ) {
        return args -> {
            seedUsers(userManagementService, appUserRepository);
            ensureSampleCustomer(customerPortfolioRepository, customerPortfolioService);
            ensureSampleApplication(creditApplicationRepository, creditApplicationService);
            ensureSampleWorkflow(creditApplicationRepository, corporateApprovalWorkflowService);
            seedBpmnRegistry(bpmnModelRepository, repositoryService, "corporateCreditApproval", "System bootstrap");
            seedBpmnRegistry(bpmnModelRepository, repositoryService, "loanApproval", "System bootstrap");
        };
    }

    private void seedUsers(UserManagementService userManagementService, AppUserRepository appUserRepository) {
        if (appUserRepository.count() > 0) {
            return;
        }

        userManagementService.saveUser(null, new AuthApiModels.UserRequest("admin", "admin123", "System Administrator", "admin@ulos.local", true, List.of("ADMIN", "WORKFLOW_ADMIN")));
        userManagementService.saveUser(null, new AuthApiModels.UserRequest("rm.user", "rm12345", "Relationship Manager", "rm@ulos.local", true, List.of("RM")));
        userManagementService.saveUser(null, new AuthApiModels.UserRequest("analyst.user", "analyst123", "Credit Analyst", "analyst@ulos.local", true, List.of("ANALYST")));
        userManagementService.saveUser(null, new AuthApiModels.UserRequest("branch.manager", "branch123", "Branch Manager", "branch@ulos.local", true, List.of("BRANCH_MANAGER")));
        userManagementService.saveUser(null, new AuthApiModels.UserRequest("regional.head", "regional123", "Regional Head", "regional@ulos.local", true, List.of("REGIONAL_HEAD")));
        userManagementService.saveUser(null, new AuthApiModels.UserRequest("committee.user", "committee123", "Credit Committee", "committee@ulos.local", true, List.of("CREDIT_COMMITTEE")));
        userManagementService.saveUser(null, new AuthApiModels.UserRequest("board.user", "board123", "Board Member", "board@ulos.local", true, List.of("BOARD_OF_DIRECTORS")));
        userManagementService.saveUser(null, new AuthApiModels.UserRequest("workflow.admin", "workflow123", "Workflow Administrator", "workflow@ulos.local", true, List.of("WORKFLOW_ADMIN")));
    }

    private void ensureSampleCustomer(
            CustomerPortfolioRepository customerPortfolioRepository,
            CustomerPortfolioService customerPortfolioService
    ) {
        if (customerPortfolioRepository.existsById(SAMPLE_CIF)) {
            return;
        }

        customerPortfolioService.save(new CustomerApiModels.CustomerPortfolioRequest(
                SAMPLE_CIF,
                "PT Meridian Alloy Indonesia",
                "PT",
                LocalDate.of(2012, 5, 14),
                "Jakarta",
                "01.234.567.8-999.000",
                "NIB-ENT-001",
                "Menara Meridian, Jakarta Selatan",
                "Karawang Industrial Estate",
                List.of(
                        new CustomerApiModels.KeyManagementItem("Raka Mahendra", "3174xxxxxx", "President Director"),
                        new CustomerApiModels.KeyManagementItem("Ayu Saraswati", "3175xxxxxx", "Finance Director")
                ),
                List.of(
                        new CustomerApiModels.ShareholderItem("Meridian Holdings Pte Ltd", new BigDecimal("70.00"), new BigDecimal("7000000000")),
                        new CustomerApiModels.ShareholderItem("PT Sinar Baja Nusantara", new BigDecimal("30.00"), new BigDecimal("3000000000"))
                ),
                List.of(
                        new CustomerApiModels.RelatedPartyItem("PARENT_COMPANY", "Meridian Holdings Pte Ltd", "SG-998877", "65-123456", "Singapore"),
                        new CustomerApiModels.RelatedPartyItem("THIRD_PARTY", "PT Garuda Leasing", "02.111.222.3-444.000", "021-889900", "Jakarta")
                ),
                List.of(
                        new CustomerApiModels.FinancialStatementItem(
                                "FS-2025-12",
                                "12/2025",
                                "AUDITED",
                                "KAP Prima Reksa",
                                null,
                                new BigDecimal("5400000000"),
                                new BigDecimal("4200000000"),
                                new BigDecimal("2600000000"),
                                new BigDecimal("9100000000"),
                                new BigDecimal("2100000000"),
                                new BigDecimal("2600000000"),
                                new BigDecimal("4100000000"),
                                new BigDecimal("8200000000"),
                                new BigDecimal("26500000000"),
                                new BigDecimal("16800000000"),
                                new BigDecimal("9700000000"),
                                new BigDecimal("4300000000"),
                                new BigDecimal("5400000000"),
                                new BigDecimal("920000000"),
                                new BigDecimal("2800000000"),
                                BigDecimal.ZERO
                        )
                ),
                List.of(
                        new CustomerApiModels.FinancialStatementItem(
                                "CONS-2025-12",
                                "12/2025",
                                "AUDITED",
                                "KAP Prima Reksa",
                                "Meridian Holdings",
                                new BigDecimal("8800000000"),
                                new BigDecimal("6100000000"),
                                new BigDecimal("3600000000"),
                                new BigDecimal("15300000000"),
                                new BigDecimal("3300000000"),
                                new BigDecimal("4100000000"),
                                new BigDecimal("6400000000"),
                                new BigDecimal("12400000000"),
                                new BigDecimal("41000000000"),
                                new BigDecimal("26100000000"),
                                new BigDecimal("14900000000"),
                                new BigDecimal("6800000000"),
                                new BigDecimal("8100000000"),
                                new BigDecimal("1330000000"),
                                new BigDecimal("4300000000"),
                                new BigDecimal("600000000")
                        )
                ),
                List.of(
                        new CustomerApiModels.BankStatementItem("Bank Mandiri", "1100009988", "12/2025", new BigDecimal("12800000000"), new BigDecimal("11800000000"), new BigDecimal("2600000000"), 0, BigDecimal.ZERO)
                ),
                List.of(
                        new CustomerApiModels.SupplierItem("PT Baja Sumber Makmur", new BigDecimal("42.00"), 45)
                ),
                List.of(
                        new CustomerApiModels.BuyerItem("PT Nusantara Komponen", new BigDecimal("38.00"), 60)
                ),
                List.of(
                        new CustomerApiModels.CompetitorItem("PT Global Alloy Manufacturing", new BigDecimal("17.50"))
                )
        ));
    }

    private void ensureSampleApplication(
            CreditApplicationRepository creditApplicationRepository,
            CreditApplicationService creditApplicationService
    ) {
        if (creditApplicationRepository.existsById(SAMPLE_APPLICATION_ID)) {
            return;
        }

        creditApplicationService.saveDraft(new ApplicationApiModels.CreditApplicationRequest(
                SAMPLE_APPLICATION_ID,
                SAMPLE_CIF,
                LocalDate.of(2026, 4, 10),
                "NEW",
                "rm.user",
                "Jakarta Corporate Branch",
                "Region West",
                "CBC Alpha",
                "NON_CONNECTED_PARTY",
                new BigDecimal("1500000000000"),
                new BigDecimal("25"),
                new BigDecimal("12000000000"),
                "C241",
                "Basic Metal Manufacturing",
                "STABLE",
                "LOW_RISK",
                "Request remains within policy and supported by operating cash flow.",
                List.of(
                        new ApplicationApiModels.FacilityItem("KMK-001", "KMK", "REVOLVING", "IDR", new BigDecimal("15000000000"), 12, LocalDate.of(2027, 4, 10), "FLOATING", new BigDecimal("12.50"), new BigDecimal("1.00"), new BigDecimal("1500000"), new BigDecimal("0.50"), new BigDecimal("2.00"), "AMORTIZATION", "Raw material financing"),
                        new ApplicationApiModels.FacilityItem("KI-001", "KREDIT_INVESTASI", "NON_REVOLVING", "IDR", new BigDecimal("6000000000"), 36, LocalDate.of(2029, 4, 10), "FIXED", new BigDecimal("11.75"), new BigDecimal("0.75"), new BigDecimal("1000000"), new BigDecimal("0.00"), new BigDecimal("1.50"), "GRACE_PERIOD", "Machine upgrade")
                ),
                List.of(
                        new ApplicationApiModels.CollateralItem("COL-001", "TANAH_BANGUNAN", "PT Meridian Alloy Indonesia", "Karawang Industrial Estate", LocalDate.of(2026, 3, 10), "KJPP Mitra", new BigDecimal("25000000000"), new BigDecimal("19000000000"), new BigDecimal("20.00"), new BigDecimal("15200000000"), "SHGB No. 8877", LocalDate.of(2032, 12, 31), "Asuransi Properti Nusantara", new BigDecimal("22000000000"), LocalDate.of(2027, 4, 10), List.of("KMK-001", "KI-001"))
                ),
                List.of(
                        new ApplicationApiModels.TboItem("TBO-001", "KMK-001", "SYARAT_PENCAIRAN", "Bukti lunas pajak tahun terakhir", LocalDate.of(2026, 5, 10), "PENDING", "RM")
                ),
                List.of(
                        new ApplicationApiModels.CovenantItem("COV-001", "FINANCIAL", "Nasabah wajib menjaga Current Ratio > 1.0x", "QUARTERLY", LocalDate.of(2026, 7, 10), "PENDING", "Warning letter and repricing")
                )
        ));
    }

    private void ensureSampleWorkflow(
            CreditApplicationRepository creditApplicationRepository,
            CorporateApprovalWorkflowService corporateApprovalWorkflowService
    ) {
        creditApplicationRepository.findById(SAMPLE_APPLICATION_ID)
                .filter(application -> (application.getProcessInstanceId() == null || application.getProcessInstanceId().isBlank())
                        && "DRAFT".equalsIgnoreCase(application.getWorkflowStatus()))
                .ifPresent(application -> corporateApprovalWorkflowService.launch(application.getApplicationId()));
    }

    private void seedBpmnRegistry(BpmnModelRepository bpmnModelRepository, RepositoryService repositoryService, String processKey, String deployedBy) throws Exception {
        if (bpmnModelRepository.findTopByProcessKeyOrderByVersionDesc(processKey).isPresent()) {
            return;
        }

        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey(processKey)
                .latestVersion()
                .singleResult();

        if (processDefinition == null) {
            return;
        }

        try (InputStream inputStream = repositoryService.getResourceAsStream(processDefinition.getDeploymentId(), processDefinition.getResourceName())) {
            if (inputStream == null) {
                return;
            }
            BpmnModelEntity entity = new BpmnModelEntity();
            entity.setProcessKey(processDefinition.getKey());
            entity.setProcessName(processDefinition.getName());
            entity.setResourceName(processDefinition.getResourceName());
            entity.setBpmnXml(new String(inputStream.readAllBytes(), StandardCharsets.UTF_8));
            entity.setDeploymentId(processDefinition.getDeploymentId());
            entity.setProcessDefinitionId(processDefinition.getId());
            entity.setVersion(processDefinition.getVersion());
            entity.setActive(true);
            entity.setDeployedBy(deployedBy);
            entity.setChangeSummary("Initial BPMN registry snapshot");
            bpmnModelRepository.save(entity);
        }
    }
}
