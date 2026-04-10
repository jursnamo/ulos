# Business Requirements Document & Technical Design
## Loan Origination System (LOS) — Commercial Banking & Corporate Banking

**Technology stack**: Java Spring Boot, Flowable, React, BPMN.io, PostgreSQL  
**Version**: 1.0 Draft  
**Document type**: Combined BRD and technical design baseline

---

## 1. Document Control

| Field | Value |
|---|---|
| Project | Loan Origination System (LOS) for Commercial Banking and Corporate Banking |
| Purpose | Baseline business requirement and technical architecture for solution delivery |
| Primary Stakeholders | Business Banking, Corporate Banking, Credit Risk, Operations, Compliance, Legal, IT, Architecture |
| Delivery Pattern | Case-based workflow with rule-driven approval and integrated document management |
| Technology Stack | Flowable + Java Spring Boot + React + BPMN.io + PostgreSQL |

## 2. Executive Summary

Bank memerlukan Loan Origination System yang terintegrasi untuk mendukung proses pengajuan kredit Commercial Banking dan Corporate Banking secara end to end. Sistem harus mampu mengelola data nasabah dan grup, analisa keuangan, struktur fasilitas, agunan, deviasi kebijakan, approval berlapis, kondisi sebelum pencairan, serta audit trail yang kuat.

Dokumen ini menggabungkan dua sudut pandang: (i) Business Requirements Document untuk kebutuhan bisnis dan operasional, serta (ii) Technical Design yang mendeskripsikan arsitektur aplikasi berbasis Flowable, Java Spring Boot, React, dan BPMN.io.

## 3. Business Objectives

- Mempercepat turnaround time pengajuan, review, dan approval kredit.
- Menyediakan single source of truth untuk data customer, grup, exposure, facility, collateral, terms and conditions, dan deviasi.
- Mengotomasi routing approval berdasarkan delegated authority, policy, rating, deviasi, dan total exposure.
- Menjamin kepatuhan terhadap BMPK, risk appetite, KYC/AML, collateral policy, dan covenant.
- Menyediakan audit trail, MIS, dan pipeline dashboard yang siap diaudit.

## 4. Scope

| In Scope | Out of Scope (phase awal) |
|---|---|
| Customer and group profile management; application/case management; financial spreading and quantitative analysis; bank statement analysis; collateral management; business rule and deviation management; facility structuring; T&C / covenant management; approval workflow; document checklist and pre-disbursement controls; management dashboard and audit trail | Core banking replacement; collections / remedial management; end-to-end legal documentation authoring; treasury pricing engine replacement; general ledger replacement; regulatory reporting replacement |

## 5. Stakeholders and User Personas

| Persona | Responsibility |
|---|---|
| Relationship Manager | Membuat aplikasi, melengkapi data nasabah, menyusun struktur fasilitas, mengusulkan deviasi, memonitor status |
| Credit Analyst | Melakukan analisa keuangan, analisa bisnis, menyusun memo kredit, validasi data dan rekomendasi |
| Credit Risk / Approver | Review usulan, melihat deviasi, memberi approval/reject/return, menentukan covenant |
| Compliance / KYC | Melakukan screening, memastikan dokumen mandatory, dan menandai compliance hold |
| Legal / Collateral | Memastikan perfection agunan, keabsahan collateral provider, dan kondisi legal sebelum pencairan |
| Operations | Memverifikasi conditions precedent, melakukan handoff ke booking/disbursement |
| Admin / Policy Owner | Mengelola rule, matrix approval, product parameter, template, dan BPMN process |

## 6. Business Requirement Modules

### 6.1 Customer / Portfolio Management

**Objective**: Menyimpan single view atas customer, grup usaha, pihak terkait, kondisi keuangan, dan profil portofolio.

**Functional requirements**
- General information: CIF/customer ID, legal entity, ownership, board, segment, RM, risk rating, status customer.
- Related party & collateral provider: mapping parent, subsidiary, affiliate, guarantor, security provider, co-borrower.
- Financial statement: upload/import audited, interim, projected; spreading ke template bank; versioning.
- Financial statement (group): konsolidasi multi-entity, intercompany elimination, group support assessment.
- Quantitative analysis: ratio engine, scoring/rating engine, stress test, DSCR/ICR/LTV/leverage.
- Bank statement: parser, turnover analysis, concentration analysis, transaction red flags.
- Business operation: supplier-buyer-competitor analysis, concentration risk, supply chain mapping.

**Business rules**
- Data mandatory harus tervalidasi sebelum aplikasi disubmit.
- Perubahan profil customer dan group relation harus memiliki audit trail.
- Override rating hanya boleh oleh user berotoritas dan wajib menyertakan justifikasi.
- Financial spread audited dan non-audited harus dibedakan secara jelas.

**Key outputs**
- Customer 360 profile
- Group exposure summary
- Financial spreading report
- Quantitative analysis sheet
- Business analysis summary

### 6.2 Application Management

**Objective**: Mengelola case pengajuan kredit dari draft sampai keputusan akhir.

**Functional requirements**
- Create new case untuk new / renewal / extension / enhancement / restructuring / annual review.
- Maintain purpose, amount requested, tenor, currency, business unit, workflow owner, checklist dokumen.
- Track application status: draft, submitted, under review, returned, pending approval, approved, declined, legal doc, ready to book.

**Business rules**
- Case tidak boleh submit jika mandatory document dan mandatory field belum lengkap.
- KYC/AML hold atau sanction screening hit harus memblok status submission sampai clear.
- Setiap perubahan struktur fasilitas setelah submission harus tercatat sebagai amendment version.

**Key outputs**
- Application summary
- Workflow history
- SLA tracking report

### 6.3 BMPK and Industry Segment

**Objective**: Memastikan proposal sesuai limit regulator/internal dan appetite sektor industri.

**Functional requirements**
- Hitung total exposure existing + proposed per obligor dan per group.
- Bandingkan dengan BMPK / internal large exposure limit.
- Klasifikasi sektor dan sub-sektor; tag preferred / neutral / restricted / prohibited.
- Tampilkan benchmark ratio per industri dan konsentrasi portofolio internal.

**Business rules**
- Breach terhadap prohibited sector harus hard stop.
- Restricted sector atau exposure concentration tinggi wajib senior approval.
- Exposure group harus mempertimbangkan funded, non-funded, dan committed undisbursed.

**Key outputs**
- BMPK check result
- Post-approval exposure simulation
- Industry eligibility result

### 6.4 Collateral Management

**Objective**: Mengelola siklus hidup agunan, nilai eligible, perfection legal, dan kecukupan coverage.

**Functional requirements**
- Maintain collateral master untuk land/building, machinery, inventory, receivables, vehicle, shares, cash collateral, guarantees.
- Link collateral ke satu atau banyak facility.
- Track market value, liquidation value, haircut, appraisal date, valuer, insurance, legal perfection, ranking.

**Business rules**
- Agunan expired appraisal, insurance expired, atau perfection belum lengkap harus memicu alert/block sesuai policy.
- LTV maksimal mengikuti policy per collateral type dan facility type.
- Shared collateral harus memiliki allocation logic yang terdokumentasi.

**Key outputs**
- Collateral coverage summary
- Legal perfection checklist
- Insurance status report

### 6.5 Business Rule Management

**Objective**: Mengatur rule approval, deviasi, policy routing, document requirements, dan SLA secara configurable.

**Functional requirements**
- Matrix approval berdasarkan amount, exposure, rating, product, tenor, unsecured flag, atau deviasi.
- Deviation rules untuk pricing, collateral shortfall, covenant waiver, restricted sector, policy breach.
- Document rule per product/collateral/customer type.
- TAT/SLA rule untuk each stage dan escalation.

**Business rules**
- Rule harus versioned, effective dated, dapat diuji sebelum publish, dan memiliki audit trail.
- Conflict antar rule harus diselesaikan dengan priority sequence yang jelas.
- Rule owner hanya dapat mengubah rule melalui maker-checker.

**Key outputs**
- Approval routing result
- Deviation register
- Escalation log

### 6.6 Facility Management

**Objective**: Menyusun struktur fasilitas yang diajukan dan memetakan term sheet secara rinci.

**Functional requirements**
- Manage multi-facility, parent line and sublimit, funded and non-funded line, revolving and non-revolving structure.
- Maintain amount, currency, tenor, pricing, fees, repayment profile, availability period, source of repayment, linked security package.
- Compare existing vs proposed facilities.

**Business rules**
- Sublimit tidak boleh melebihi parent line.
- Pricing floor, max tenor, repayment type, dan product eligibility mengikuti policy parameter.
- Special structure tertentu memerlukan approval tambahan atau legal review.

**Key outputs**
- Facility term sheet
- Structure summary
- Pricing summary

### 6.7 TBO Management

**Objective**: Menilai total value relationship dan peluang cross-sell di luar lending.

**Functional requirements**
- Capture wallet share, CASA, FX, trade, payroll, supply chain, fee-based income potential.
- Map existing vs target relationship revenue.
- Link concession pricing dengan opportunity value.

**Business rules**
- Concession pricing di atas threshold wajib memperlihatkan justifikasi TBO.
- Large exposure dengan return rendah wajib escalation atau relationship action plan.

**Key outputs**
- Relationship profitability projection
- Cross-sell dashboard

### 6.8 T&C Management

**Objective**: Mengelola terms, conditions, dan covenant sepanjang lifecycle proposal hingga pre-disbursement dan post-approval tracking.

**Functional requirements**
- Maintain standard T&C library dan special conditions.
- Classify conditions precedent to approval, precedent to disbursement, conditions subsequent, financial covenant, information covenant, affirmative covenant, negative covenant.
- Track fulfillment status dan evidence document.

**Business rules**
- Conditions precedent yang belum terpenuhi harus memblok pencairan.
- Covenant breach dan waiver harus memiliki workflow approval dan histori keputusan.
- Mandatory T&C dapat diturunkan dari product, rating, atau collateral type.

**Key outputs**
- T&C register
- Covenant status dashboard
- Waiver history

## 7. Non-Functional Requirements

| Requirement | Target |
|---|---|
| Availability | Sistem tersedia minimal 99.5% pada jam operasional yang disepakati |
| Performance | P95 response time untuk layar normal <= 3 detik; workflow action <= 5 detik |
| Scalability | Mendukung pertumbuhan user, case, dan rule tanpa perubahan arsitektur mayor |
| Auditability | Setiap perubahan data kritikal, keputusan approval, dan perubahan rule harus mempunyai audit trail lengkap |
| Security | SSO, RBAC, maker-checker, encryption in transit and at rest, segregasi duty |
| Configurability | Rule, workflow, approval matrix, checklist dokumen, dan template memo harus dapat diubah tanpa redeploy besar |
| Observability | System log, metrics, process metrics, dan alert operasional harus tersedia |
| Localization | Dukungan format mata uang, tanggal, timezone, dan bahasa Indonesia/English bila diperlukan |

---

# 8. Technical Design Overview

Solusi direkomendasikan menggunakan arsitektur modular monolith terlebih dahulu untuk mempercepat delivery dan menekan kompleksitas operasional. Flowable digunakan sebagai workflow/process engine, Java Spring Boot sebagai backend business services, React sebagai front-end user interface, dan BPMN.io sebagai visual modeler untuk authoring/maintaining BPMN definition.

## 8.1 Architectural Principles

- Case-centric: satu application/case menjadi anchor utama untuk semua data underwriting dan workflow.
- API-first: semua capability diekspos melalui REST API yang konsisten dan terdokumentasi.
- Workflow-driven: human task, service task, approval routing, dan escalation dijalankan melalui Flowable BPMN.
- Configurable: rule, checklist, parameter, approval matrix, dan template dikelola sebagai konfigurasi.
- Event-aware: perubahan state penting mempublikasikan domain event untuk dashboard, notification, atau integrasi.

## 8.2 Logical Architecture

| Layer | Components | Notes |
|---|---|---|
| Presentation | React portal, task inbox, case workspace, admin console, BPMN modeler shell (BPMN.io) | Front-end consumes Spring Boot APIs and Flowable task/process APIs |
| Application / Domain | Customer, Application, Facility, Collateral, T&C, Rule, Document, Integration, Reporting modules | Encapsulates business logic and orchestrates workflow variables |
| Workflow | Flowable Process Engine, Task Service, Form/Identity integration, Job Executor | Executes BPMN processes, timers, escalations, and task assignments |
| Data | PostgreSQL application schema, Flowable engine schema, object storage/document repository | Stores transactional data, process instances, documents, and audit trails |
| Integration | Core banking, CIF/KYC, AML, SLIK/bureau, appraisal, DMS, email/notification, pricing engine | Via REST, message queue, SFTP, or adapter services as available |

## 8.3 Recommended Deployment Topology

- React application deployed as static web app behind reverse proxy/API gateway.
- Spring Boot application deployed in 2+ instances for high availability.
- Flowable engine embedded inside Spring Boot application for simpler deployment and transaction consistency.
- PostgreSQL primary-replica setup recommended for production.
- Object storage or enterprise DMS digunakan untuk binary documents.
- Centralized log and monitoring: ELK/OpenSearch, Prometheus/Grafana, atau tool setara.

## 8.4 Module to Component Mapping

| Business Module | Spring Component | Core Domain Objects |
|---|---|---|
| Customer Management | customer-service, group-service, party-service | CustomerAggregate, GroupAggregate, RelatedParty |
| Application Management | application-service | ApplicationCase, ApplicationStatus, SubmissionSnapshot |
| Financial Analysis | financial-service, statement-parser | FinancialStatement, SpreadVersion, QuantitativeResult |
| Collateral Management | collateral-service | Collateral, Valuation, Insurance, LegalPerfection |
| Facility Management | facility-service | Facility, Sublimit, PricingTerm, RepaymentTerm |
| T&C Management | condition-service | Condition, Covenant, WaiverRequest |
| Rule Management | rule-service | ApprovalMatrixRule, DeviationRule, DocumentRule |
| Workflow | workflow-service / flowable integration | ProcessInstanceBinding, TaskAssignment |
| Documents | document-service | DocumentRecord, ChecklistItem, EvidenceLink |

# 9. Core Domain Model

| Entity | Description | Important Attributes | Relationships |
|---|---|---|---|
| Customer | Representasi debitur atau legal entity | customerId, name, legalForm, segment, rating, status | Belongs to Group; has FinancialStatement; can have many ApplicationCase |
| Group | Grup usaha / parent-child structure | groupId, name, ultimateParent, exposureSummary | Has many Customers and RelatedParties |
| ApplicationCase | Case pengajuan kredit | applicationId, type, status, purpose, totalRequested, workflowKey | References Customer/Group; has Facilities, Collateral links, Conditions |
| Facility | Satu line/sublimit/facility dalam case | facilityId, type, amount, currency, tenor, pricing | Belongs to ApplicationCase; linked to Collateral and Conditions |
| Collateral | Agunan | collateralId, owner, type, value, haircut, perfectionStatus | Can support one or many Facilities |
| FinancialStatement | Sumber data keuangan | statementId, period, auditedFlag, sourceType, version | Belongs to Customer or Group |
| QuantitativeResult | Hasil analisa kuantitatif | score, rating, dscr, leverage, decisionBand | Derived from FinancialStatement and BankStatement |
| Condition | Terms and conditions / covenant | conditionId, category, mandatoryFlag, dueDate, status | Linked to Facility or ApplicationCase |
| Deviation | Policy exception | deviationId, type, severity, justification, approvalStatus | Belongs to ApplicationCase and affects routing |
| DocumentRecord | Dokumen terdaftar | documentId, category, source, expiryDate, verificationStatus | Attached to Customer, Collateral, or ApplicationCase |

# 10. Workflow and BPMN Design

Flowable bertindak sebagai workflow/process engine. BPMN definition dimodelkan dan dipelihara dengan BPMN.io, kemudian disimpan ke repository process definition. Aplikasi Spring Boot meng-start process instance, menyuplai process variables, dan menyelesaikan human task serta service task.

## 10.1 Key BPMN Processes

| Process Key | Purpose | Typical Swimlanes |
|---|---|---|
| los-application-origination | Proses utama dari draft sampai approval/decline | RM, Credit Analyst, Risk, Approver, Legal, Ops |
| los-deviation-approval | Proses approval deviasi policy | Requester, Risk, Business Head, Committee |
| los-cp-fulfillment | Tracking condition precedent sebelum booking/disbursement | Ops, Legal, Collateral Officer |
| los-covenant-waiver | Permintaan waiver/amendment covenant/T&C | RM, Credit Risk, Approver |
| los-annual-review | Review berkala untuk facility existing | RM, Analyst, Risk, Approver |

## 10.2 Main Origination Flow (BPMN narrative)

- Start event: RM membuat atau membuka application case.
- Service task: load customer profile, existing exposure, group structure, and mandatory checklist.
- User task: RM melengkapi application form, proposed facility, collateral, TBO, dan upload dokumen.
- Service task: rule pre-validation untuk mandatory fields, KYC status, BMPK pre-check, dan sector appetite.
- User task: credit analyst melakukan financial spreading dan business analysis.
- Service task: calculate quantitative result, risk rating recommendation, deviation detection, and approval matrix.
- Exclusive gateway: apakah ada deviasi / restricted condition / high exposure? Jika ya, route ke jalur approval yang sesuai.
- User task: approver review, request change, approve, or decline.
- Parallel gateway: setelah approved, jalankan legal/collateral perfection dan condition precedent fulfillment secara paralel bila diperlukan.
- Exclusive gateway: semua CP terpenuhi? Jika ya, mark ready for booking; jika tidak, remain pending.
- End event: approved and ready for booking, atau declined/closed.

## 10.3 Flowable Implementation Pattern

- BPMN process definition disimpan dalam repository dan diberi versioning.
- Process variables minimal: applicationId, customerId, groupId, totalExposure, totalRequested, rating, deviationFlags, approvalLevel, segment, industryCode, cpPendingCount.
- Service task menggunakan JavaDelegate/Spring bean untuk menjalankan domain logic.
- Human task menggunakan assignee/candidate group berbasis role dan organizational mapping.
- Timer boundary event digunakan untuk SLA escalation.
- Call activity dapat dipakai untuk deviation process atau CP fulfillment sub-process.

## 10.4 BPMN Model Governance with BPMN.io

- Admin process dapat mengedit BPMN melalui BPMN.io-based modeler di admin console.
- Model disimpan dalam format BPMN 2.0 XML; publish hanya dapat dilakukan melalui approval internal/change control.
- Setiap process definition mempunyai metadata: process key, version, effective date, owner, notes, rollback target.
- Production publish sebaiknya dibatasi pada role khusus dan dilengkapi smoke test terhadap sample variables.

## 10.5 Example BPMN Elements Mapping

| BPMN Element | Use in LOS | Implementation Note |
|---|---|---|
| Start Event | Origination started | Triggered via API when case submitted |
| User Task | Analyst review / approver review / legal review | Assignee and candidate group from role mapping |
| Service Task | BMPK check / rule evaluation / notification / integration call | Spring bean delegate or external worker |
| Exclusive Gateway | Deviation yes/no, approve/reject | Decision from process variables |
| Parallel Gateway | Parallel review or parallel CP checks | Used carefully to avoid orphaned tasks |
| Boundary Timer | SLA escalation | Escalate or notify supervisor |
| Call Activity | Deviation sub-process / covenant waiver | Reusable process with own lifecycle |
| End Event | Approved, declined, ready-for-booking | State synchronized back to application case |

# 11. API and Integration Design

## 11.1 Representative REST APIs

| Method | Endpoint | Purpose | Notes |
|---|---|---|---|
| POST | /api/applications | Create application case | Returns applicationId and initial status |
| GET | /api/applications/{id} | Get case workspace data | Aggregates summary, facilities, collateral, conditions, tasks |
| POST | /api/applications/{id}/submit | Submit case to workflow | Starts or advances process instance |
| POST | /api/applications/{id}/recalculate | Re-run quantitative analysis and rule evaluation | Used after structure/data changes |
| POST | /api/tasks/{taskId}/complete | Complete human task | Carries decision and comments |
| GET | /api/tasks/inbox | Get task inbox for current user | Supports filters and pagination |
| POST | /api/collaterals | Create/update collateral | Validation on type and owner |
| POST | /api/facilities | Create/update facility | Re-check parent line and pricing rules |
| POST | /api/rules/evaluate | Evaluate rule set against payload | Can support simulation mode |
| POST | /api/bpmn/deploy | Deploy approved BPMN definition | Admin only |

## 11.2 External Integrations

| System | Direction | Interface Type | Data/Use Case |
|---|---|---|---|
| CIF / Customer Master | Inbound | REST / MQ / batch | Fetch customer profile and maintain reference consistency |
| KYC / AML / Sanction Screening | Inbound/Outbound | REST | Screening result, compliance hold |
| SLIK / Credit Bureau | Inbound | API / file | External credit information |
| Core Banking | Bidirectional | REST / MQ / file | Existing exposure, booking status, facility reference |
| Appraisal System | Inbound | REST / file | Appraisal values and valuer data |
| Document Management | Bidirectional | REST / object storage adapter | Store/retrieve evidence and mandatory docs |
| Notification | Outbound | SMTP / SMS / push | Task alerts, SLA escalation, status updates |

# 12. Data Design

## 12.1 Key Tables

| Table / Aggregate | Primary Purpose | Key Keys / Columns |
|---|---|---|
| customer | Master customer data | customer_id, group_id, segment, rating, status |
| customer_party_relation | Related party mapping | customer_id, related_party_id, relation_type |
| application_case | Main application record | application_id, customer_id, type, status, total_requested, process_instance_id |
| application_snapshot | Version and audit snapshot | application_id, version_no, payload_json, created_by |
| facility | Facility term data | facility_id, application_id, parent_facility_id, type, amount, tenor |
| collateral | Collateral master | collateral_id, owner_party_id, type, value, perfection_status |
| facility_collateral_link | Security package mapping | facility_id, collateral_id, allocation_pct |
| financial_statement | Financial source | statement_id, owner_type, owner_id, period_end, audited_flag, version_no |
| quantitative_result | Calculated score and ratios | application_id, rating, score, dscr, leverage, decision_band |
| condition_item | T&C / covenant records | condition_id, application_id/facility_id, category, status, due_date |
| deviation_item | Policy exceptions | deviation_id, application_id, type, severity, status |
| document_record | Document metadata | document_id, owner_type, owner_id, category, uri, expiry_date, verification_status |
| audit_log | Business audit | entity_type, entity_id, action, old_value, new_value, actor, timestamp |

## 12.2 State Model

Application status minimum yang direkomendasikan: `DRAFT -> IN_REVIEW -> PENDING_APPROVAL -> APPROVED / DECLINED -> CP_PENDING -> READY_FOR_BOOKING -> BOOKED / CLOSED`.

# 13. Security, Controls, and Audit

- SSO integration dengan corporate identity provider (SAML/OIDC).
- Role-based access control untuk RM, Analyst, Approver, Risk, Legal, Ops, Admin.
- Attribute-based checks tambahan untuk branch/region/business unit bila diperlukan.
- Maker-checker untuk perubahan policy rule, BPMN deployment, dan data referensi kritikal.
- Audit trail untuk perubahan field kritikal, upload dokumen, completion task, approval decision, dan deployment BPMN.
- Electronic signature dan sign-off comment dapat diintegrasikan pada tahap lanjut.

# 14. Front-End Design (React)

- Dashboard: pipeline, SLA, pending approvals, outstanding CP, deviation trend.
- Task Inbox: personal inbox dan team queue.
- Case Workspace: customer summary, tabs/modul, facility, collateral, documents, conditions, workflow timeline.
- Credit Analysis Workspace: financial spread, ratios, business analysis, memo preview.
- Admin Console: rule management, product parameter, approval matrix, BPMN deployment, reference data.
- BPMN Modeler Screen: embedding BPMN.io untuk visual edit/view process definition.

State management dapat menggunakan React Query + form library seperti React Hook Form. UI harus mendukung autosave, draft indicator, optimistic fetch, dan server-side validation feedback.

# 15. Implementation Guidance

| Phase | Scope | Main Deliverables | Comment |
|---|---|---|---|
| Phase 1 | Customer, application, workflow, facility, basic collateral, document checklist, approval | MVP usable for new/renewal standard cases | Prioritize speed and control |
| Phase 2 | Financial spread, quantitative analysis, BMPK, industry, T&C, deviation | Underwriting depth and stronger policy enforcement | Adds risk control depth |
| Phase 3 | Bank statement parser, TBO, advanced dashboards, annual review, covenant waiver | Broader relationship and monitoring capability | Higher automation value |
| Phase 4 | Advanced integrations, e-sign, complex corporate structures, syndication-ready patterns | Enterprise-grade maturity | Optional by target operating model |

# 16. Key Risks and Mitigations

| Risk | Impact | Mitigation |
|---|---|---|
| Customer and group master data inconsistency | Wrong exposure and approval result | Establish master data governance and reconciliation with CIF |
| Rule sprawl / policy complexity | Opaque approval routing and maintenance difficulty | Use rule catalog, versioning, simulation, and strict ownership |
| Workflow design too complex early on | Slow delivery and high support burden | Start with modular monolith and a limited process library |
| Document completeness not enforced | Operational leakage and audit finding | Mandatory checklist + blocking conditions + dashboard |
| Poor BPMN governance | Production incident after workflow change | Change control, publish approval, regression test, rollback plan |

# 17. Appendix A – Suggested BPMN Variables

`applicationId, customerId, groupId, applicationType, segment, industryCode, totalRequested, totalExposure, rating, score, deviationFlags, approvalLevel, restrictedSectorFlag, bmpkStatus, cpPendingCount, currentApproverRole, bookingReadinessStatus`

# 18. Appendix B – Suggested Spring Boot Package Layout

```text
com.bank.los
  ├── application
  │    ├── api
  │    ├── service
  │    ├── domain
  │    ├── repository
  │    └── workflow
  ├── customer
  ├── facility
  ├── collateral
  ├── financial
  ├── condition
  ├── rules
  ├── integration
  ├── security
  ├── shared
  └── config
```

# 19. Appendix C – Suggested Decision on Architecture Style

Rekomendasi awal adalah modular monolith dengan domain modules yang jelas, karena lebih cocok untuk proses bisnis kredit yang sangat transaksi-sensitif dan erat dengan workflow engine. Service decomposition ke microservices dapat dipertimbangkan setelah volume, tim, dan kebutuhan integrasi meningkat.