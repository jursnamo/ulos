# LOS Technical Design
## Commercial Banking & Corporate Banking

Version: 1.0 Draft  
Format: Technical Design  
Target Stack: Java Spring Boot, Flowable, React, BPMN.io  

---

## 1. Purpose

Dokumen ini merangkum technical design end-to-end untuk Loan Origination System (LOS) Commercial Banking dan Corporate Banking berdasarkan pembahasan sebelumnya. Fokus desain mencakup:

- Customer Portfolio Management
- Create Application / Application Management
- Facility Master dan Facility Builder
- Collateral Management
- Drawdown Conditions
- Global Terms & Conditions (T&C)
- TBO Management
- SLIK Checking
- To Be Obtained (required document / required data)
- Workflow orchestration dengan Flowable
- UI architecture dengan React
- API dan domain design dengan Spring Boot

Dokumen ini dimaksudkan sebagai baseline untuk BA, Solution Architect, Backend Engineer, Frontend Engineer, BPM Designer, dan QA.

---

## 2. Design Principles

### 2.1 Master-driven, not hardcoded
Semua variasi penting harus didorong oleh master data dan rule engine, bukan hardcoded per screen. Ini terutama berlaku untuk:

- facility per segment
- required document / data
- drawdown conditions
- global T&C
- SLIK subject type dan risk flags
- collateral type specific fields
- workflow routing dan approval escalation

### 2.2 Layered domain model
Domain utama dibagi menjadi:

- Customer / Group
- Application
- Facility
- Collateral
- Credit Checking
- Required Documents / Data
- Conditions & Covenants
- Opportunity & Relationship Value
- Workflow / Decision / Audit

### 2.3 Separation of concern
Beberapa objek yang sering tercampur harus dipisah:

- Global T&C vs Facility T&C vs Drawdown Conditions
- To Be Obtained vs Submitted Documents vs Verification Result
- SLIK raw response vs SLIK summary
- Collateral master vs valuation vs legal perfection vs insurance vs linkage
- Facility master vs facility transaction vs drawdown utilization

### 2.4 Single application workspace
Satu application harus menjadi workspace utama untuk melihat:

- customer dan related parties
- SLIK status
- credit check summary
- facility structure
- collateral dan coverage
- documents outstanding
- deviation / policy checklist
- drawdown readiness

---

## 3. High-Level Architecture

## 3.1 Logical Architecture

```text
[ React Web App ]
        |
        v
[ API Gateway / BFF ]
        |
        v
[ Spring Boot LOS Services ]
        |
        +--> [ Flowable Process Engine ]
        +--> [ Rule Evaluation Service ]
        +--> [ Document Service / DMS Adapter ]
        +--> [ Customer / CIF Adapter ]
        +--> [ Core Banking / Host Adapter ]
        +--> [ SLIK Integration Adapter ]
        +--> [ Collateral / Appraisal Adapter ]
        +--> [ Notification Service ]
        +--> [ Reporting / Analytics Service ]
        |
        v
[ PostgreSQL / Oracle ]
```

## 3.2 Main Components

### Frontend
- React
- React Router
- React Query / RTK Query
- Form engine for dynamic fields
- BPMN preview / status visualization
- role-based screens and actions

### Backend
- Spring Boot REST API
- Spring Security / OAuth2 / SSO
- JPA / MyBatis depending on reporting complexity
- Flowable Engine for BPMN / task orchestration
- Validation layer for business and data rules
- Integration adapters for external systems

### Workflow
- Flowable BPMN for business process
- Flowable CMMN optional for case-heavy scenarios
- BPMN.io for process modeling and visualization

### Data
- transactional schema
- master schema
- audit schema
- document metadata schema

---

## 4. Core Domain Overview

## 4.1 Core Aggregates

### Customer Aggregate
- customer
- related party
- addresses
- contacts
- ratings
- business profile
- banking relationship

### Application Aggregate
- application header
- application purpose
- compliance fields
- policy checklist
- prescreening result
- flow routing decision

### Facility Aggregate
- facility line
- facility detail
- pricing
- repayment
- drawdown condition set
- facility summary

### Collateral Aggregate
- collateral master
- collateral subtype detail
- valuation
- insurance
- legal perfection
- facility linkage
- coverage result

### Credit Check Aggregate
- SLIK subject
- SLIK request
- raw response
- subject summary
- application summary

### Requirement Aggregate
- requirement template
- requested item
- submitted item
- verification
- deficiency / outstanding

### T&C Aggregate
- global T&C
- facility T&C
- covenant / monitoring item
- waiver
- breach tracking

### TBO Aggregate
- TBO header
- business profile snapshot
- product opportunity lines
- wallet capture
- income projection
- action plan

---

## 5. Module Design

# 5.1 Customer Portfolio Management

## Objectives
Menyimpan single source of truth terkait profil nasabah, grup, pihak terkait, orientasi bisnis, rating, hubungan bank, dan informasi dasar underwriting.

## Functional Scope
- create / update customer portfolio
- maintain company profile
- maintain business address and other address
- maintain person to contact
- maintain business orientation
- maintain external and internal rating
- maintain relationship with bank / FI lain
- maintain connected party information

## Key Entities
- `customer`
- `customer_address`
- `customer_contact_person`
- `customer_business_orientation`
- `customer_rating_external`
- `customer_rating_internal`
- `customer_bank_relationship`
- `customer_connected_party`

## Proposed Tables
- `trx_customer`
- `trx_customer_address`
- `trx_customer_contact_person`
- `trx_customer_business_orientation`
- `trx_customer_external_rating`
- `trx_customer_internal_rating`
- `trx_customer_bank_relationship`
- `trx_customer_connected_party`

## Important Design Notes
- company and person subjects should be supported
- support multiple addresses
- support multiple contact persons
- support business orientation repeating rows for sales and purchases by geography
- keep snapshot fields that may later be copied into application

## API Examples
- `POST /api/customers`
- `GET /api/customers/{id}`
- `PUT /api/customers/{id}`
- `GET /api/customers/{id}/related-parties`
- `POST /api/customers/{id}/ratings`

## UI Design
Tabs:
- General Information
- Business Orientation
- Ratings
- Relationship with Bank
- Connected Party
- Addresses
- Contacts
- Narrative / Background

---

# 5.2 Related Party & Group Model

## Objectives
Mendukung relasi borrower dengan director, commissioner, shareholder, UBO, guarantor, group company, dan connected party.

## Main Functions
- record related company and individual
- classify relationship type
- determine whether subject should be used for SLIK, T&C, or document requirement
- support group exposure aggregation

## Key Fields
- related party type
- relationship type
- ownership percentage
- guarantor flag
- collateral provider flag
- SLIK mandatory flag
- document required flag
- legal signatory flag

## Tables
- `trx_related_party`
- `trx_related_party_relationship`
- `trx_group_structure`

---

# 5.3 Application Management / Create Application

## Objectives
Menjadi root container dari seluruh proses pengajuan kredit.

## Functional Scope
- create application from customer portfolio
- capture application purpose and control fields
- capture remarks and referral
- capture BMPK summary
- capture industry MAT and sector exposure
- maintain application status
- route to prescreening and checklist

## Main Entities
- `application_header`
- `application_purpose`
- `application_control`
- `application_referral`
- `application_bmpk`
- `application_industry_mat`
- `application_sector_exposure`

## Proposed Tables
- `trx_application`
- `trx_application_purpose`
- `trx_application_referral`
- `trx_application_bmpk`
- `trx_application_industry_mat`
- `trx_application_sector_exposure`
- `trx_application_status_history`

## Key Statuses
- DRAFT
- SUBMITTED
- UNDER_REVIEW
- PENDING_PRESCREENING
- RETURNED
- PENDING_APPROVAL
- APPROVED
- REJECTED
- CANCELLED
- READY_FOR_BOOKING

## API Examples
- `POST /api/applications`
- `GET /api/applications/{id}`
- `PUT /api/applications/{id}`
- `POST /api/applications/{id}/submit`
- `GET /api/applications/{id}/summary`

## UI Design
Sections:
- Application Header
- Application Purpose
- Control Fields
- Remarks & Referral
- BMPK Details
- Industry MAT
- Sector Exposure

---

# 5.4 Credit Check Result and Policy Checklist

## Objectives
Menampung hasil checking eksternal/internal dan policy exception summary pada level application.

## Components
- credit check result grid
- policy monitoring checklist
- high risk checklist
- special consideration checklist
- deviation matrix

## Main Entities
- `application_credit_check_subject`
- `application_policy_checklist`
- `application_deviation_rac`
- `application_risk_flag`

## Tables
- `trx_application_credit_check`
- `trx_policy_checklist`
- `trx_policy_exception_item`
- `trx_deviation_rac`

---

# 5.5 Facility Management

## Objectives
Mengelola struktur produk, fasilitas, limit, pricing, repayment, maturity, dan segment-specific forms.

## Design Approach
Gunakan **dynamic facility master**. Setiap facility ditentukan oleh:

- product family
- facility code
- segment applicability (SME / COMBA / COBA)
- field mapping
- mandatory rules
- drawdown condition templates
- T&C templates

## Core Master Tables
- `mst_product`
- `mst_facility`
- `mst_segment`
- `mst_facility_segment`
- `mst_field_group`
- `mst_field_definition`
- `mst_facility_field_mapping`
- `mst_facility_rule`

## Transaction Tables
- `trx_application_facility`
- `trx_facility_pricing`
- `trx_facility_repayment`
- `trx_facility_account_reference`
- `trx_facility_limit_check`

## Suggested Facility Categories
### SME
- OD
- PRK / WCL
- TLF
- Asset Finance
- Machinery Finance
- BG Bid Bond
- BG Performance Bond

### COMBA
- Revolving Credit Facility
- Term Loan
- Bank Guarantee
- LC Import / Export
- Trust Receipt
- Supplier Financing
- Receivable Financing
- Bill Discounting

### COBA
- Umbrella Line
- Project Finance
- Syndicated Participation
- FX Line
- Hedging Line
- Structured Working Capital

## UI Design
- facility listing grid
- product selector
- facility selector
- dynamic detail form
- readonly facility summary

---

# 5.6 Collateral Management

## Objectives
Mengelola jaminan lintas tipe dengan dukungan subtype fields, valuation, legal perfection, insurance, dan linkage.

## Design Pattern
Gunakan common header + type specific detail + supporting layers.

## Common Layers
- collateral master
- owner / provider
- valuation
- legal perfection
- insurance
- facility linkage
- monitoring event

## Core Tables
- `trx_collateral`
- `trx_collateral_owner`
- `trx_collateral_valuation`
- `trx_collateral_legal_perfection`
- `trx_collateral_insurance`
- `trx_collateral_facility_link`
- `trx_collateral_monitoring_event`

## Type-specific Tables
- `trx_collateral_land_building`
- `trx_collateral_machine`
- `trx_collateral_vehicle`
- `trx_collateral_inventory`
- `trx_collateral_receivable`
- `trx_collateral_cash`
- `trx_collateral_security`
- `trx_collateral_guarantee`

## Key Outputs
- collateral listing
- collateral linkage preview
- collateral coverage summary
- company group collateral coverage

## Important Design Notes
- support own collateral and cross collateral
- support shared collateral across facilities
- separate market value, liquidation value, eligible value, haircut
- legal perfection and insurance must be versioned or historized

---

# 5.7 Drawdown Conditions

## Objectives
Mendefinisikan syarat pencairan / utilization gate pada level facility.

## Design Pattern
**Condition Template -> Facility Condition -> Evidence -> Verification -> Waiver -> Drawdown Gate**

## Categories
- general pre-disbursement
- security / collateral
- documentation
- financial contribution
- compliance / regulatory
- utilization-specific
- post-drawdown deferred conditions

## Master Tables
- `mst_drawdown_condition_group`
- `mst_drawdown_condition`
- `mst_facility_drawdown_condition_template`

## Transaction Tables
- `trx_facility_drawdown_condition`
- `trx_facility_drawdown_condition_evidence`
- `trx_facility_drawdown_waiver`

## Key Fields
- condition code
- condition category
- stage
- mandatory flag
- blocking flag
- due date
- status
- evidence type
- verifier role
- waiver flag

## Key Statuses
- DRAFT
- PENDING_SUBMISSION
- SUBMITTED
- UNDER_REVIEW
- FULFILLED
- REJECTED
- WAIVED
- NOT_APPLICABLE
- EXPIRED

## UI Design
- drawdown condition summary card
- condition grid
- detail drawer for evidence and history

---

# 5.8 Global T&C Management

## Objectives
Mengelola syarat dan ketentuan level application / borrower / group yang berlaku lintas facility.

## Design Principle
Pisahkan:
- Global T&C
- Facility T&C
- Drawdown Condition
- Covenant Monitoring

## Categories
- legal & corporate
- compliance & regulatory
- financial reporting undertaking
- operational undertaking
- security / collateral undertaking
- covenant
- event-based obligation

## Master Tables
- `mst_tc_category`
- `mst_global_tc`
- `mst_facility_tc_template`

## Transaction Tables
- `trx_application_global_tc`
- `trx_application_tc_evidence`
- `trx_application_tc_waiver`
- `trx_application_tc_breach`

## Key Fields
- tc code
- category
- scope
- mandatory flag
- blocking flag
- monitoring flag
- pre-disbursement flag
- post-disbursement flag
- due date
- breach flag
- waiver flag

## Statuses
- DRAFT
- PENDING
- FULFILLED
- ACTIVE_MONITORING
- WAIVED
- NOT_APPLICABLE
- BREACHED
- CLOSED

---

# 5.9 TBO Management

## Objectives
TBO di sini berarti **Total Business Opportunity**. Modul ini memodelkan total wallet relationship nasabah dan peluang cross-sell/non-lending.

## Main Layers
- relationship-level TBO
- product-level TBO
- captured vs uncaptured
- commercial value / income projection
- action plan

## Tables
- `trx_tbo_header`
- `trx_tbo_business_profile`
- `trx_tbo_product_opportunity`
- `trx_tbo_wallet_capture`
- `trx_tbo_income_projection`
- `trx_tbo_competitor_info`
- `trx_tbo_action_plan`

## Key Fields
### Header
- tbo scope
- period
- RM
- originating unit
- status

### Business profile snapshot
- annual sales
- procurement volume
- import/export volume
- operating turnover
- average CASA
- payroll volume
- suppliers / buyers count

### Opportunity lines
- lending opportunity
- trade opportunity
- BG opportunity
- cash management opportunity
- FX opportunity
- payroll opportunity

### Wallet capture
- captured amount
- wallet share
- wallet gap
- competitor bank

### Commercial value
- estimated lending income
- estimated fee income
- estimated FX income
- total relationship income

### Pipeline
- opportunity stage
- probability
- target close date
- next action
- owner

---

# 5.10 SLIK Checking

## Objectives
Mendukung pemeriksaan kredit untuk borrower, related party, director, commissioner, shareholder, guarantor, dan group company.

## Design Pattern
**Subject List -> Request -> Raw Response -> Normalization -> Subject Summary -> Application Summary -> Risk Flags**

## Subject Types
- borrower company
- borrower individual
- director
- commissioner
- shareholder / UBO
- personal guarantor
- corporate guarantor
- related company

## Core Tables
- `trx_slik_subject`
- `trx_slik_request`
- `trx_slik_response_raw`
- `trx_slik_facility_detail`
- `trx_slik_lender_summary`
- `trx_slik_subject_summary`
- `trx_slik_application_summary`
- `trx_slik_exception_flag`

## Key Summary Outputs
### Subject Summary
- total lender count
- total facility count
- total plafond
- total outstanding
- total past due
- worst collectibility
- max DPD
- restructured count
- write-off count
- adverse flag
- overall risk level

### Application Summary
- total subjects checked
- subjects with adverse findings
- highest DPD across all subjects
- total external outstanding
- related party negative findings
- consolidated SLIK risk flag

## UI Design
Tabs:
- SLIK Subject List
- Subject Summary
- Lender Summary
- Facility Detail
- Application Consolidation

---

# 5.11 To Be Obtained (Required Documents / Data)

## Objectives
Mengelola dokumen dan data yang perlu disubmit calon nasabah / pihak terkait ke bank selama proses underwriting.

## Design Pattern
**Requirement Template -> Requested Item -> Submitted Item -> Verification -> Outstanding Tracker**

## Requirement Scope
- customer
- application
- facility
- collateral
- related party

## Important Distinction
Item dapat berupa:
- document
- structured data
- both

## Master Tables
- `mst_requirement_category`
- `mst_document_requirement`
- `mst_requirement_mapping`
- `mst_requirement_field_definition`

## Transaction Tables
- `trx_required_item`
- `trx_required_item_submission`
- `trx_required_item_verification`
- `trx_required_item_exception`
- `trx_required_item_history`

## Key Fields
- requirement code
- requirement category
- requirement scope
- requested from
- item nature
- mandatory flag
- blocking flag
- due date
- submission status
- verification status
- deficiency type
- expiry date
- copy/original indicator

## Categories
- legal entity documents
- ownership & management documents
- financial documents
- business & operational documents
- banking & compliance documents
- facility-specific documents
- collateral documents

---

## 6. Workflow Design with Flowable

# 6.1 Main Process Overview

```text
Start
  -> Create / Select Customer Portfolio
  -> Create Application
  -> Maintain Related Parties
  -> Build SLIK Subject List
  -> Request SLIK / Receive Result
  -> Prescreening
  -> Policy Checklist / Deviation RAC
  -> Build Facilities
  -> Maintain Collateral and Linkage
  -> Capture To Be Obtained Items
  -> Maintain Global T&C / Drawdown Conditions
  -> Credit Review
  -> Approval Routing
  -> Approved / Returned / Rejected
  -> Pre-booking / Drawdown Readiness
End
```

# 6.2 Suggested BPMN Processes

## Process 1 - Application Onboarding
- start event
- create application task
- validate mandatory fields service task
- user task: complete customer / related party data
- gateway: SLIK required?
- if yes -> call SLIK subprocess
- user task: complete policy checklist
- service task: calculate exposure / BMPK / initial risk flags
- end or move to facility build

## Process 2 - SLIK Check Subprocess
- prepare subject list
- user review subject list
- validate subject data
- send SLIK request
- wait for response
- parse raw response
- build subject summary
- evaluate risk flags
- update credit check result

## Process 3 - Facility and Collateral Build
- user task: add facilities
- user task: select / add collateral
- service task: calculate collateral linkage and coverage
- service task: generate standard drawdown conditions
- service task: generate standard facility T&C

## Process 4 - Document and Condition Readiness
- generate To Be Obtained items
- user task: upload / submit documents
- ops/legal review
- gateway: all mandatory docs accepted?
- generate blocking / outstanding summary
- user task: resolve gaps or request waiver

## Process 5 - Approval Process
- compile application summary
- route by approval matrix
- user task: reviewer / analyst
- user task: approver 1
- gateway: escalate?
- user task: approver 2 / committee
- end state: approved / rejected / returned

# 6.3 Process Variables

Examples:
- `applicationId`
- `customerId`
- `segmentCode`
- `applicationPurpose`
- `slikRequired`
- `slikAdverseFlag`
- `bmpkBreached`
- `policyExceptionCount`
- `facilityCount`
- `collateralCoveragePct`
- `mandatoryDocOutstandingCount`
- `blockingDrawdownConditionCount`
- `approvalLevel`
- `isCommitteeRequired`

# 6.4 Task Ownership
- RM / Account Officer
- Credit Analyst
- BCM / Risk Reviewer
- Operations
- Legal
- Compliance
- Approver / Committee

---

## 7. Spring Boot Service Design

# 7.1 Suggested Service Boundaries

- `customer-service`
- `application-service`
- `facility-service`
- `collateral-service`
- `slik-service`
- `document-requirement-service`
- `tc-service`
- `tbo-service`
- `workflow-service`
- `masterdata-service`
- `integration-service`
- `reporting-service`

Untuk implementasi awal, services tersebut bisa tetap berada dalam satu monolith modular dengan package/domain separation.

# 7.2 Suggested Package Structure

```text
com.bank.los
  ├── common
  ├── security
  ├── masterdata
  ├── customer
  ├── application
  ├── facility
  ├── collateral
  ├── slik
  ├── requirement
  ├── tc
  ├── tbo
  ├── workflow
  ├── integration
  ├── audit
  └── reporting
```

# 7.3 Layering Pattern

For each domain:
- controller
- dto
- service
- validator
- repository
- entity
- mapper
- policy / rule evaluator

# 7.4 Sample APIs

## Application
- `POST /api/applications`
- `PUT /api/applications/{id}`
- `POST /api/applications/{id}/submit`
- `GET /api/applications/{id}/workspace`

## Facility
- `POST /api/applications/{id}/facilities`
- `GET /api/applications/{id}/facilities`
- `PUT /api/facilities/{facilityId}`
- `GET /api/facilities/{facilityId}/drawdown-conditions`

## Collateral
- `POST /api/applications/{id}/collaterals`
- `POST /api/collaterals/{collateralId}/valuations`
- `POST /api/collaterals/{collateralId}/legal-perfections`
- `POST /api/collaterals/{collateralId}/linkages`

## SLIK
- `POST /api/applications/{id}/slik-subjects/auto-generate`
- `POST /api/slik-subjects/{subjectId}/request`
- `GET /api/slik-subjects/{subjectId}/summary`
- `GET /api/applications/{id}/slik-summary`

## Requirement
- `POST /api/applications/{id}/required-items/generate`
- `POST /api/required-items/{itemId}/submissions`
- `POST /api/required-items/{itemId}/verify`
- `GET /api/applications/{id}/required-items/outstanding`

## T&C
- `POST /api/applications/{id}/global-tc/generate`
- `PUT /api/application-tc/{tcItemId}`
- `POST /api/application-tc/{tcItemId}/waive`
- `POST /api/application-tc/{tcItemId}/breach`

---

## 8. React Frontend Design

# 8.1 UI Composition

Main workspace layout:
- left: application navigation / modules
- center: active module content
- right: summary / warnings / blockers

# 8.2 Suggested Main Screens

- Customer Portfolio Screen
- Related Party Screen
- Application Setup Screen
- Credit Check Result Screen
- Policy Checklist Screen
- Facility Builder Screen
- Collateral Builder Screen
- SLIK Screen
- Required Documents / Data Screen
- Global T&C Screen
- Drawdown Conditions Screen
- Approval / Task Screen
- Readonly Application Summary Screen

# 8.3 Dynamic Form Engine

Use metadata driven rendering for:
- facility-specific forms
- collateral subtype forms
- required data collection
- T&C condition parameters
- drawdown condition evidence

Suggested metadata fields:
- fieldCode
- label
- dataType
- componentType
- required
- readonly
- defaultValue
- optionsSource
- validationRules
- visibilityRule
- orderNo

# 8.4 Component Recommendations

- `DynamicFormRenderer`
- `SectionCard`
- `SummaryBadge`
- `StatusChip`
- `ChecklistGrid`
- `DocumentSubmissionPanel`
- `SLIKSummaryCard`
- `CollateralCoverageCard`
- `FacilitySummaryPanel`
- `AuditTrailDrawer`

---

## 9. BPMN.io Usage

## 9.1 Purpose
BPMN.io digunakan untuk:
- process design oleh BA / architect
- process preview di admin console
- process visualization di application runtime

## 9.2 Recommended Usage Pattern
- BPMN model maintained in admin/backoffice
- BPMN XML versioned per process code
- deployed to Flowable via deployment pipeline or admin module
- runtime task instances linked to BPMN activity ids

## 9.3 Key Artifacts
- `APP_ONBOARDING.bpmn`
- `SLIK_CHECK_SUBPROCESS.bpmn`
- `FACILITY_COLLATERAL_BUILD.bpmn`
- `DOC_READINESS_PROCESS.bpmn`
- `APPROVAL_ROUTING_PROCESS.bpmn`

---

## 10. Data Design Recommendations

# 10.1 Schema Separation

- `los_mst` for master data
- `los_trx` for transactional data
- `los_audit` for audit and history
- `los_int` for integration raw requests/responses

# 10.2 Audit Trail
Every mutable domain should have:
- created by / date
- updated by / date
- version no
- change reason if sensitive
- full history or event log for critical entities

Critical entities requiring strong audit:
- application header
- facility amount / pricing / maturity
- collateral value / legal perfection
- SLIK request/result
- T&C / waiver / breach
- drawdown condition status
- document verification

# 10.3 Snapshot Strategy
At submission / approval points, create snapshots for:
- customer profile summary
- facility proposal summary
- collateral coverage summary
- SLIK application summary
- outstanding document summary
- T&C summary

This prevents reporting inconsistency when source records change later.

---

## 11. Rule Engine Design

## 11.1 Rule Types
- eligibility rules
- mandatory field rules
- workflow routing rules
- approval matrix rules
- document generation rules
- SLIK adverse flag rules
- collateral haircut rules
- drawdown block rules
- T&C / covenant breach rules

## 11.2 Execution Strategy
- synchronous validation for form save / submit
- asynchronous recomputation for summaries
- Flowable gateways for workflow routing
- configurable master tables for thresholds and mappings

## 11.3 Examples
- if facility = BG then underlying contract required
- if collateral type = land & building then certificate + appraisal + insurance required
- if SLIK max DPD > threshold then approval level escalate
- if mandatory required item not accepted then application cannot submit
- if global T&C blocking item breached then drawdown blocked

---

## 12. Security and Access Control

## 12.1 Role Examples
- RM / Account Officer
- Credit Analyst
- BCM / Risk
- Operations
- Legal
- Compliance
- Approver
- Admin Master Data
- Workflow Admin

## 12.2 Access Principles
- role-based authorization
- field-level readonly control for sensitive values
- action-level authorization for submit / approve / waive / override
- data masking for sensitive IDs and account numbers
- SLIK result access only to authorized roles

---

## 13. Integration Design

## 13.1 Likely Integrations
- CIF / customer master
- core banking / host
- DMS / content repository
- SLIK gateway
- internal rating engine
- appraisal service / collateral service
- notification / email / work queue
- reference master sources

## 13.2 Adapter Pattern
Every external integration should have:
- request mapper
- response mapper
- retry policy
- idempotency key
- error normalization
- raw request / response storage

## 13.3 SLIK Integration Notes
- subject-based request model
- async callback or polling supported
- preserve raw payloads
- normalize to lender and facility level tables

---

## 14. Non-Functional Requirements

## 14.1 Performance
- application workspace should load summary data quickly
- large tables should be paginated / lazy loaded
- summary cards should use precomputed views where needed

## 14.2 Reliability
- retry and compensation for external integrations
- process recovery for failed service tasks
- versioned deployments for BPMN

## 14.3 Observability
- correlation id per request
- workflow instance tracing
- integration logs with masked sensitive fields
- audit dashboards

## 14.4 Scalability
- metadata-driven modules should support addition of new facility, T&C, drawdown conditions without code rewrite

---

## 15. Recommended Delivery Phasing

## Phase 1
- Customer Portfolio
- Related Party
- Create Application
- Facility Builder basic
- Collateral basic
- SLIK request and summary
- Required Documents / Data basic
- Approval workflow basic

## Phase 2
- Global T&C and drawdown conditions
- advanced collateral linkage and coverage
- dynamic facility master rollout
- policy checklist and deviation RAC automation
- TBO module

## Phase 3
- covenant monitoring
- advanced wallet share analytics
- broader external integrations
- advanced exception and waiver dashboard

---

## 16. Open Design Decisions

Beberapa keputusan yang masih perlu dipastikan di level business / architecture:

1. apakah implementasi awal monolith modular atau microservice
2. database standard yang dipakai
3. apakah DMS sudah tersedia atau perlu embedded document storage sementara
4. apakah SLIK response realtime, async callback, atau batch retrieval
5. apakah BPM model di-manage oleh IT only atau business admin juga
6. apakah TBO dipakai hanya sebagai relationship planning atau ikut approval justification
7. apakah covenant monitoring masuk LOS atau handoff ke post-booking system

---

## 17. Conclusion

Desain LOS yang baik untuk Commercial Banking dan Corporate Banking harus menempatkan application sebagai workspace utama, namun seluruh variasi kompleksitas harus didorong oleh metadata, rules, dan workflow yang configurable.

Area yang paling penting untuk dibuat master-driven sejak awal adalah:
- facility definition
- collateral subtype model
- required documents / data
- SLIK subject and summary model
- global T&C
- drawdown conditions
- approval routing

Dengan pendekatan ini, LOS dapat mendukung kebutuhan SME, Commercial Banking, dan Corporate Banking tanpa harus membuat layar dan logika baru secara hardcoded untuk setiap variasi produk.
