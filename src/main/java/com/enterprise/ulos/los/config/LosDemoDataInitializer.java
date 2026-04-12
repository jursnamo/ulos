package com.enterprise.ulos.los.config;

import com.enterprise.ulos.domain.bpmn.BpmnModelEntity;
import com.enterprise.ulos.los.entity.CustomerEntity;
import com.enterprise.ulos.los.entity.PrecheckSessionEntity;
import com.enterprise.ulos.los.model.ApplicationApiModels;
import com.enterprise.ulos.los.model.AuthApiModels;
import com.enterprise.ulos.los.model.CustomerApiModels;
import com.enterprise.ulos.los.repository.AppUserRepository;
import com.enterprise.ulos.los.repository.CreditApplicationRepository;
import com.enterprise.ulos.los.repository.CustomerRepository;
import com.enterprise.ulos.los.repository.CustomerPortfolioRepository;
import com.enterprise.ulos.los.repository.PrecheckSessionRepository;
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
import java.time.LocalDateTime;
import java.util.List;

@Configuration
public class LosDemoDataInitializer {

    private static final String SAMPLE_CIF = "CIF-ENT-001";
    private static final String SAMPLE_APPLICATION_ID = "APP-ENT-001";
    private static final String SEED_REF_DATE = "20260412";

    @Bean
    CommandLineRunner seedDemoData(
            UserManagementService userManagementService,
            AppUserRepository appUserRepository,
            CustomerPortfolioRepository customerPortfolioRepository,
            CreditApplicationRepository creditApplicationRepository,
            PrecheckSessionRepository precheckSessionRepository,
            CustomerRepository customerRepository,
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
            ensureSamplePrechecks(precheckSessionRepository, customerRepository);
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

    private void ensureSamplePrechecks(
            PrecheckSessionRepository precheckSessionRepository,
            CustomerRepository customerRepository
    ) {
        List<CustomerEntity> customers = customerRepository.findAll()
                .stream()
                .filter(customer -> customer.getCifNumber() != null && !customer.getCifNumber().isBlank())
                .sorted((left, right) -> left.getCifNumber().compareToIgnoreCase(right.getCifNumber()))
                .limit(10)
                .toList();

        if (customers.isEmpty()) {
            return;
        }

        for (int index = 0; index < customers.size(); index++) {
            seedPrecheckPackForCustomer(precheckSessionRepository, customers.get(index), index + 1);
        }
    }

    private void seedPrecheckPackForCustomer(
            PrecheckSessionRepository precheckSessionRepository,
            CustomerEntity customer,
            int ordinal
    ) {
        String token = customerToken(customer, ordinal);
        LocalDateTime now = LocalDateTime.now().minusHours((long) ordinal * 3);
        String linkedAppId = SAMPLE_CIF.equalsIgnoreCase(customer.getCifNumber()) ? SAMPLE_APPLICATION_ID : null;
        String customerName = safe(customer.getCompanyName(), "Unknown Customer");

        upsertPrecheck(
                precheckSessionRepository,
                customer,
                "SLIK-" + SEED_REF_DATE + "-" + token + "-01",
                "SLIK",
                "COMPLETED",
                now.minusDays(8),
                now.minusDays(8).plusMinutes(26),
                "SLIK selesai - profil pembayaran baik, collectibility mayoritas 1",
                sampleSlikCompletedResult(customer, token, 1),
                linkedAppId,
                "Seeded SLIK completed (batch 1) for " + customerName
        );

        upsertPrecheck(
                precheckSessionRepository,
                customer,
                "SLIK-" + SEED_REF_DATE + "-" + token + "-02",
                "SLIK",
                "COMPLETED",
                now.minusDays(3),
                now.minusDays(3).plusMinutes(14),
                "SLIK re-check selesai - terdapat perbaikan DPD dibanding periode sebelumnya",
                sampleSlikCompletedResult(customer, token, 2),
                linkedAppId,
                "Seeded SLIK completed (batch 2) for " + customerName
        );

        upsertPrecheck(
                precheckSessionRepository,
                customer,
                "SLIK-" + SEED_REF_DATE + "-" + token + "-03",
                "SLIK",
                "IN_PROGRESS",
                now.minusHours(12),
                null,
                "SLIK request sedang diproses oleh bureau",
                sampleSlikInProgressResult(customer, token),
                null,
                "Seeded SLIK in-progress request for " + customerName
        );

        upsertPrecheck(
                precheckSessionRepository,
                customer,
                "COLLATERAL-" + SEED_REF_DATE + "-" + token + "-01",
                "COLLATERAL",
                "COMPLETED",
                now.minusDays(5),
                now.minusDays(5).plusMinutes(43),
                "Appraisal selesai - nilai agunan tervalidasi",
                sampleCollateralCompletedResult(customer, token, 1),
                linkedAppId,
                "Seeded collateral completed (batch 1) for " + customerName
        );

        upsertPrecheck(
                precheckSessionRepository,
                customer,
                "COLLATERAL-" + SEED_REF_DATE + "-" + token + "-02",
                "COLLATERAL",
                "COMPLETED",
                now.minusDays(2),
                now.minusDays(2).plusMinutes(38),
                "Appraisal re-check selesai untuk update market value",
                sampleCollateralCompletedResult(customer, token, 2),
                linkedAppId,
                "Seeded collateral completed (batch 2) for " + customerName
        );

        upsertPrecheck(
                precheckSessionRepository,
                customer,
                "COLLATERAL-" + SEED_REF_DATE + "-" + token + "-03",
                "COLLATERAL",
                "REQUESTED",
                now.minusHours(4),
                null,
                "Request appraisal baru sedang menunggu penilai",
                sampleCollateralRequestedResult(customer, token),
                null,
                "Seeded collateral requested for " + customerName
        );
    }

    private void upsertPrecheck(
            PrecheckSessionRepository precheckSessionRepository,
            CustomerEntity customer,
            String checkRef,
            String checkType,
            String status,
            LocalDateTime requestedAt,
            LocalDateTime completedAt,
            String summary,
            String resultJson,
            String linkedApplicationId,
            String notes
    ) {
        PrecheckSessionEntity entity = precheckSessionRepository.findByCheckRef(checkRef).orElseGet(PrecheckSessionEntity::new);
        entity.setCheckRef(checkRef);
        entity.setCheckType(checkType);
        entity.setCustomer(customer);
        entity.setStatus(status);
        entity.setRequestedAt(requestedAt);
        entity.setCompletedAt(completedAt);
        entity.setResultSummary(summary);
        entity.setResultJson(resultJson);
        entity.setLinkedApplicationId(linkedApplicationId);
        entity.setNotes(notes);
        precheckSessionRepository.save(entity);
    }

    private String sampleSlikCompletedResult(CustomerEntity customer, String token, int variant) {
        String companyName = jsonEscape(safe(customer.getCompanyName(), "Unknown Company"));
        String companyTax = jsonEscape(safe(customer.getTaxId(), safe(customer.getCifNumber(), "UNKNOWN-ID")));
        String companyAddress = jsonEscape(safe(customer.getLocation(), "Jakarta"));
        String refMain = "A-" + token;
        String refDirector = "P-" + token + "01";
        String refCommissioner = "P-" + token + "02";
        String refShareholder = "G-" + token + "03";
        String riskFlag = variant == 1 ? "Low" : "Moderate";
        String maxDpd = variant == 1 ? "1" : "7";
        String collectibility = variant == 1 ? "1" : "2";
        String outstandingMain = variant == 1 ? "4000" : "4300";
        String monthlyObligation = variant == 1 ? "33" : "36";

        return """
                {
                  "subjects": [
                    {
                      "id": "SLIK-SUB-001",
                      "refNum": "%s",
                      "debtorType": "A",
                      "name": "%s",
                      "gender": "",
                      "dob": "2012-05-14",
                      "pob": "Jakarta",
                      "ktp": "",
                      "npwp": "%s",
                      "isCompany": "1",
                      "motherName": "",
                      "address": "%s",
                      "city": "%s",
                      "zipCode": "12940",
                      "isBatch": "0",
                      "reqPurpose": "00",
                      "facilities": [
                        {
                          "bankInstitution": "PT Bank Jtrust Indonesia d/h Bank Mutiara",
                          "facility": "Modal Kerja",
                          "revolvingTerm": "Revolving",
                          "initialLimit": "2000",
                          "osIdrMio": "%s",
                          "interestPct": "8.50",
                          "monthlyObligationIdrMio": "%s",
                          "collectability": "%s",
                          "facilitySince": "2022-01-01",
                          "startDate": "2022-01-01",
                          "endDate": "2026-12-31",
                          "maturity": "2026-12-31",
                          "currency": "IDR",
                          "facilityStatus": "ACTIVE",
                          "restructured": "No",
                          "restructuredFreq": "0",
                          "maxDpd24MonthLatest": "%s",
                          "tenorMonths": "60",
                          "notes": "Pembayaran lancar."
                        },
                        {
                          "bankInstitution": "PT Bank CIMB Niaga Tbk",
                          "facility": "Modal Kerja",
                          "revolvingTerm": "Revolving",
                          "initialLimit": "2000",
                          "osIdrMio": "%s",
                          "interestPct": "10.00",
                          "monthlyObligationIdrMio": "%s",
                          "collectability": "%s",
                          "facilitySince": "2021-04-10",
                          "startDate": "2021-04-10",
                          "endDate": "2027-04-10",
                          "maturity": "2027-04-10",
                          "currency": "IDR",
                          "facilityStatus": "ACTIVE",
                          "restructured": "No",
                          "restructuredFreq": "0",
                          "maxDpd24MonthLatest": "%s",
                          "tenorMonths": "72",
                          "notes": "Exposure terbesar pada modal kerja."
                        }
                      ],
                      "collaterals": [
                        {
                          "jenisAgunanKe": "Tanah dan Bangunan",
                          "nomorAgunan": "COL-SLIK-001",
                          "nilaiAgunanMenurutLJK": "25000",
                          "prosentaseParipasu": "100",
                          "tanggalUpdate": "2026-04-08",
                          "jenisPengikatan": "SHGB",
                          "jenisPengikatanKet": "Akta Hak Tanggungan",
                          "tanggalPengikatan": "2023-07-15",
                          "namaPemilikAgunan": "%s",
                          "alamatAgunan": "%s",
                          "kabKotaLokasiAgunan": "Karawang",
                          "kabKotaLokasiAgunanKet": "Jawa Barat",
                          "tglPenilaianPelapor": "2026-04-07",
                          "peringkatAgunan": "A",
                          "kodeLembagaPemeringkat": "KJPP-01",
                          "lembagaPemeringkat": "KJPP Mitra Penilai",
                          "buktiKepemilikan": "SHGB 8877",
                          "nilaiAgunanNjop": "22000",
                          "nilaiAgunanIndep": "24000",
                          "namaPenilaiIndep": "Mitra Penilai",
                          "asuransi": "Asuransi Properti Nusantara",
                          "tanggalPenilaianPenilaiIndependen": "2026-04-07",
                          "keterangan": "Agunan utama untuk fasilitas modal kerja."
                        }
                      ]
                    },
                    {
                      "id": "SLIK-SUB-002",
                      "refNum": "%s",
                      "debtorType": "P",
                      "name": "Ayu Saraswati",
                      "gender": "Female",
                      "dob": "1987-09-19",
                      "pob": "Bandung",
                      "ktp": "3175XXXXXXXXXXXX",
                      "npwp": "12.345.678.9-111.000",
                      "isCompany": "0",
                      "motherName": "Dewi Pranoto",
                      "address": "Menteng Dalam, Jakarta Selatan",
                      "city": "Jakarta Selatan",
                      "zipCode": "12870",
                      "isBatch": "0",
                      "reqPurpose": "00",
                      "facilities": [
                        {
                          "bankInstitution": "PT Bank Hibank Indonesia",
                          "facility": "Kartu Kredit",
                          "revolvingTerm": "Revolving",
                          "initialLimit": "200",
                          "osIdrMio": "55",
                          "interestPct": "2.25",
                          "monthlyObligationIdrMio": "6",
                          "collectability": "1",
                          "facilitySince": "2020-01-18",
                          "startDate": "2020-01-18",
                          "endDate": "2028-01-18",
                          "maturity": "2028-01-18",
                          "currency": "IDR",
                          "facilityStatus": "ACTIVE",
                          "restructured": "No",
                          "restructuredFreq": "0",
                          "maxDpd24MonthLatest": "0",
                          "tenorMonths": "96",
                          "notes": "Tagihan kartu kredit rutin."
                        }
                      ],
                      "collaterals": []
                    },
                    {
                      "id": "SLIK-SUB-003",
                      "refNum": "%s",
                      "debtorType": "P",
                      "name": "Raka Mahendra",
                      "gender": "Male",
                      "dob": "1983-04-22",
                      "pob": "Surabaya",
                      "ktp": "3174XXXXXXXXXXXX",
                      "npwp": "11.223.344.5-666.000",
                      "isCompany": "0",
                      "motherName": "Ratna Wibowo",
                      "address": "Kemang Timur, Jakarta Selatan",
                      "city": "Jakarta Selatan",
                      "zipCode": "12730",
                      "isBatch": "0",
                      "reqPurpose": "00",
                      "facilities": [
                        {
                          "bankInstitution": "PT Bank Danamon Indonesia",
                          "facility": "Kredit Multiguna",
                          "revolvingTerm": "Term",
                          "initialLimit": "850",
                          "osIdrMio": "390",
                          "interestPct": "11.50",
                          "monthlyObligationIdrMio": "11",
                          "collectability": "1",
                          "facilitySince": "2022-07-01",
                          "startDate": "2022-07-01",
                          "endDate": "2028-06-30",
                          "maturity": "2028-06-30",
                          "currency": "IDR",
                          "facilityStatus": "ACTIVE",
                          "restructured": "No",
                          "restructuredFreq": "0",
                          "maxDpd24MonthLatest": "0",
                          "tenorMonths": "72",
                          "notes": "Kredit personal untuk aset produktif."
                        }
                      ],
                      "collaterals": []
                    },
                    {
                      "id": "SLIK-SUB-004",
                      "refNum": "%s",
                      "debtorType": "G",
                      "name": "PT Nusantara Group Holding",
                      "gender": "",
                      "dob": "2008-03-11",
                      "pob": "Jakarta",
                      "ktp": "",
                      "npwp": "09.887.766.5-123.000",
                      "isCompany": "1",
                      "motherName": "",
                      "address": "Sudirman Central District",
                      "city": "Jakarta Pusat",
                      "zipCode": "10220",
                      "isBatch": "0",
                      "reqPurpose": "00",
                      "facilities": [
                        {
                          "bankInstitution": "PT Bank Negara Indonesia Tbk",
                          "facility": "Bank Garansi",
                          "revolvingTerm": "Revolving",
                          "initialLimit": "5000",
                          "osIdrMio": "1200",
                          "interestPct": "2.80",
                          "monthlyObligationIdrMio": "8",
                          "collectability": "1",
                          "facilitySince": "2021-01-10",
                          "startDate": "2021-01-10",
                          "endDate": "2026-12-31",
                          "maturity": "2026-12-31",
                          "currency": "IDR",
                          "facilityStatus": "ACTIVE",
                          "restructured": "No",
                          "restructuredFreq": "0",
                          "maxDpd24MonthLatest": "0",
                          "tenorMonths": "72",
                          "notes": "Fasilitas non-funded untuk proyek EPC."
                        }
                      ],
                      "collaterals": []
                    }
                  ],
                  "summary": {
                    "totalFacility": 11,
                    "activeFacility": 7,
                    "maxCollectibilityKol": %s,
                    "maxDpdLast24Months": %s,
                    "estimatedMonthlyObligationIdrMio": %s,
                    "riskFlag": "%s"
                  }
                }
                """.formatted(
                refMain,
                companyName,
                companyTax,
                companyAddress,
                companyAddress,
                variant == 1 ? "0" : "250",
                variant == 1 ? "0" : "2400",
                collectibility,
                maxDpd,
                outstandingMain,
                monthlyObligation,
                collectibility,
                maxDpd,
                companyName,
                companyAddress,
                refDirector,
                refCommissioner,
                refShareholder,
                collectibility,
                maxDpd,
                monthlyObligation,
                riskFlag
        );
    }

    private String sampleSlikInProgressResult(CustomerEntity customer, String token) {
        String companyName = jsonEscape(safe(customer.getCompanyName(), "Unknown Company"));
        String companyTax = jsonEscape(safe(customer.getTaxId(), safe(customer.getCifNumber(), "UNKNOWN-ID")));
        return """
                {
                  "subjects": [
                    {
                      "id": "SLIK-SUB-REQ-001",
                      "refNum": "A-%s",
                      "debtorType": "A",
                      "name": "%s",
                      "idType": "NPWP",
                      "idNo": "%s",
                      "status": "Requested",
                      "lastInquiryDate": "2026-04-11"
                    },
                    {
                      "id": "SLIK-SUB-REQ-002",
                      "refNum": "P-%s11",
                      "debtorType": "P",
                      "name": "Dewi Lestari",
                      "idType": "KTP",
                      "idNo": "3173XXXXXXXXXXXX",
                      "status": "Requested",
                      "lastInquiryDate": "2026-04-11"
                    }
                  ],
                  "summary": {
                    "riskFlag": "Pending",
                    "notes": "Waiting for bureau response."
                  }
                }
                """.formatted(token, companyName, companyTax, token);
    }

    private String sampleCollateralCompletedResult(CustomerEntity customer, String token, int variant) {
        String companyName = jsonEscape(safe(customer.getCompanyName(), "Unknown Company"));
        String city = jsonEscape(safe(customer.getLocation(), "Jakarta"));
        String buildingValue = variant == 1 ? "25000" : "26800";
        String buildingEligible = variant == 1 ? "19000" : "20500";
        String machineValue = variant == 1 ? "6800" : "7000";
        String machineEligible = variant == 1 ? "4760" : "4900";
        return """
                {
                  "collaterals": [
                    {
                      "id": "COL-DET-%s-01",
                      "code": "COL-%s-01",
                      "collateralType": "Land & Building",
                      "description": "Factory and warehouse for %s",
                      "ownershipType": "Borrower-owned",
                      "currency": "IDR",
                      "marketValue": "%s",
                      "eligibleValue": "%s",
                      "haircut": "24",
                      "notes": "Independent valuer confirms good marketability.",
                      "jenisAgunanKe": "Tanah dan Bangunan",
                      "nomorAgunan": "COL-%s-01",
                      "nilaiAgunanMenurutLJK": "%s",
                      "prosentaseParipasu": "100",
                      "tanggalUpdate": "2026-04-09",
                      "jenisPengikatan": "SHGB",
                      "jenisPengikatanKet": "Akta Hak Tanggungan",
                      "tanggalPengikatan": "2023-07-15",
                      "namaPemilikAgunan": "%s",
                      "alamatAgunan": "Industrial Estate %s",
                      "kabKotaLokasiAgunan": "%s",
                      "kabKotaLokasiAgunanKet": "Jawa Barat",
                      "tglPenilaianPelapor": "2026-04-08",
                      "peringkatAgunan": "A",
                      "kodeLembagaPemeringkat": "KJPP-01",
                      "lembagaPemeringkat": "KJPP Mitra Penilai",
                      "buktiKepemilikan": "SHGB 8877",
                      "nilaiAgunanNjop": "22000",
                      "nilaiAgunanIndep": "24000",
                      "namaPenilaiIndep": "Mitra Penilai",
                      "asuransi": "Asuransi Properti Nusantara",
                      "tanggalPenilaianPenilaiIndependen": "2026-04-08",
                      "keterangan": "Collateral eligible for primary coverage."
                    },
                    {
                      "id": "COL-DET-%s-02",
                      "code": "COL-%s-02",
                      "collateralType": "Machine",
                      "description": "Hot rolling production line",
                      "ownershipType": "Borrower-owned",
                      "currency": "IDR",
                      "marketValue": "%s",
                      "eligibleValue": "%s",
                      "haircut": "30",
                      "notes": "Machinery insured and in good condition."
                    }
                  ],
                  "summary": {
                    "totalMarketValueIdrMio": %s,
                    "totalEligibleValueIdrMio": %s,
                    "riskFlag": "Moderate"
                  }
                }
                """.formatted(
                token, token, companyName, buildingValue, buildingEligible, token, buildingValue, companyName, city, city,
                token, token, machineValue, machineEligible,
                Integer.parseInt(buildingValue) + Integer.parseInt(machineValue),
                Integer.parseInt(buildingEligible) + Integer.parseInt(machineEligible)
        );
    }

    private String sampleCollateralRequestedResult(CustomerEntity customer, String token) {
        String companyName = jsonEscape(safe(customer.getCompanyName(), "Unknown Company"));
        return """
                {
                  "collaterals": [
                    {
                      "id": "COL-REQ-%s-01",
                      "code": "COL-NEW-%s-01",
                      "collateralType": "Vehicle",
                      "description": "Operational truck fleet - %s",
                      "ownershipType": "Borrower-owned",
                      "currency": "IDR",
                      "marketValue": "0",
                      "eligibleValue": "0",
                      "haircut": "35",
                      "notes": "Waiting for appraiser assignment."
                    }
                  ],
                  "summary": {
                    "status": "Pending",
                    "notes": "Queued for appraisal scheduling."
                  }
                }
                """.formatted(token, token, companyName);
    }

    private String customerToken(CustomerEntity customer, int ordinal) {
        String raw = safe(customer.getCifNumber(), "CUST" + ordinal)
                .replaceAll("[^A-Za-z0-9]", "")
                .toUpperCase();
        if (raw.isBlank()) {
            raw = "CUST" + ordinal;
        }
        return raw.length() > 12 ? raw.substring(raw.length() - 12) : raw;
    }

    private String safe(String value, String fallback) {
        if (value == null || value.isBlank()) {
            return fallback;
        }
        return value;
    }

    private String jsonEscape(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
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
