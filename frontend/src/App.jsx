import { useEffect, useMemo, useState } from "react";

const tabs = [
  { key: "portfolio", label: "Customer 360" },
  { key: "application", label: "Application" },
  { key: "tasks", label: "Approval Inbox" },
  { key: "rules", label: "Rule Studio" }
];

const companyTypes = ["PT", "CV", "FA", "KOPERASI", "YAYASAN"];
const applicationTypes = ["NEW", "RENEWAL", "TOP_UP", "RESTRUCTURING", "TAKE_OVER"];
const industryOutlooks = ["POSITIVE", "STABLE", "NEGATIVE"];
const ruleTypes = ["HARD_STOP", "WARNING", "DEVIATION"];
const metricKeys = ["AVAILABLE_LIMIT", "PROPOSED_EXPOSURE", "DER", "COLLATERAL_COVERAGE", "CURRENT_RATIO"];
const operators = ["GT", "GTE", "LT", "LTE", "EQ"];
const routingTypes = ["BRANCH_MANAGER", "REGIONAL_HEAD", "CREDIT_COMMITTEE", "BOARD_OF_DIRECTORS"];

const currencyFormatter = new Intl.NumberFormat("id-ID", {
  style: "currency",
  currency: "IDR",
  maximumFractionDigits: 0
});

function createCustomerForm() {
  return {
    cifNumber: "",
    companyName: "",
    companyType: "PT",
    dateOfEstablishment: "",
    placeOfEstablishment: "",
    taxId: "",
    businessLicense: "",
    officeAddress: "",
    factoryAddress: "",
    keyManagementJson: sampleJson([{ name: "Direktur Utama", nationalIdNumber: "3275xxxx", title: "President Director" }]),
    shareholdersJson: sampleJson([{ name: "PT Holding Nusantara", ownershipPercentage: 75, shareNominal: 7500000000 }]),
    relatedPartiesJson: sampleJson([{ relationType: "PARENT_COMPANY", name: "PT Holding Nusantara", identityNumber: "01.234.567.8-999.000", contactDetails: "021-555000", address: "Jakarta" }]),
    financialStatementsJson: sampleJson([blankFinancialStatement()]),
    consolidatedFinancialStatementsJson: sampleJson([blankFinancialStatement(true)]),
    bankStatementsJson: sampleJson([{ bankName: "Bank ABC", accountNumber: "0011223344", period: "12/2025", totalInflow: 12000000000, totalOutflow: 10000000000, averageBalance: 2500000000, chequeReturnCount: 0, chequeReturnNominal: 0 }]),
    suppliersJson: sampleJson([{ supplierName: "PT Supplier Baja", purchasePercentage: 40, paymentTermsDays: 45 }]),
    buyersJson: sampleJson([{ buyerName: "PT Buyer Retail", salesPercentage: 35, paymentTermsDays: 60 }]),
    competitorsJson: sampleJson([{ competitorName: "PT Kompetitor Satu", estimatedMarketShare: 15 }])
  };
}

function createApplicationForm() {
  return {
    applicationId: "",
    customerCif: "",
    applicationDate: new Date().toISOString().slice(0, 10),
    applicationType: "NEW",
    rmName: "",
    branchName: "",
    regionName: "",
    cbcName: "",
    groupRelationshipStatus: "NON_CONNECTED_PARTY",
    coreCapitalBank: "",
    maxLendingLimitPercentage: "25",
    existingExposureGroup: "",
    biSectorCode: "",
    subSectorDescription: "",
    industryOutlook: "STABLE",
    esgGreenFinancingStatus: "",
    justificationNote: "",
    facilitiesJson: sampleJson([blankFacility()]),
    collateralsJson: sampleJson([blankCollateral()]),
    tboItemsJson: sampleJson([blankTbo()]),
    covenantsJson: sampleJson([blankCovenant()])
  };
}

function createRuleForm() {
  return {
    ruleId: "",
    ruleName: "",
    ruleType: "WARNING",
    metricKey: "DER",
    operator: "GT",
    thresholdValue: "",
    actionRouting: "REGIONAL_HEAD",
    conditionExpression: "",
    baseApprover: "BRANCH_MANAGER",
    escalatedApprover: "REGIONAL_HEAD",
    committeeApprover: "CREDIT_COMMITTEE",
    boardApprover: "BOARD_OF_DIRECTORS",
    justificationRequired: "true",
    active: "true",
    messageTemplate: ""
  };
}

function blankFinancialStatement(consolidated = false) {
  return {
    statementId: consolidated ? "CONS-2025-12" : "STANDALONE-2025-12",
    period: "12/2025",
    auditStatus: "AUDITED",
    auditorName: "KAP Contoh",
    groupHoldingName: consolidated ? "Holding Example" : null,
    cash: 5000000000,
    accountsReceivable: 3500000000,
    inventory: 2200000000,
    fixedAssets: 8500000000,
    accountsPayable: 1800000000,
    shortTermLoan: 2500000000,
    longTermLoan: 4500000000,
    totalEquity: 7000000000,
    salesRevenue: 24000000000,
    cogs: 15000000000,
    grossProfit: 9000000000,
    operatingExpenses: 4200000000,
    ebitda: 4800000000,
    interestExpense: 900000000,
    netIncome: 2500000000,
    intercompanyElimination: consolidated ? 500000000 : 0
  };
}

function blankFacility() {
  return {
    facilityCode: "KMK-001",
    facilityName: "KMK",
    revolvingStatus: "REVOLVING",
    currency: "IDR",
    limitAmount: 15000000000,
    tenorMonths: 12,
    maturityDate: "2027-04-10",
    interestRateType: "FLOATING",
    interestRate: 12.5,
    provisionFee: 1,
    adminFee: 1500000,
    commitmentFee: 0.5,
    penaltyFee: 2,
    repaymentType: "AMORTIZATION",
    purposeOfLoan: "Working capital for raw material purchase"
  };
}

function blankCollateral() {
  return {
    collateralId: "COL-001",
    collateralType: "TANAH_BANGUNAN",
    ownerName: "PT Example Manufacturing",
    locationAddress: "Bekasi Industrial Estate",
    appraisalDate: "2026-03-01",
    appraiserName: "KJPP Contoh",
    marketValue: 20000000000,
    liquidationValue: 15000000000,
    marginOfAdvancePercentage: 20,
    bankableValue: 12000000000,
    legalDocumentInfo: "SHGB No. 1001",
    legalDocumentExpiryDate: "2030-12-31",
    insuranceName: "Asuransi Umum",
    insuranceCoverageValue: 18000000000,
    insuranceExpiryDate: "2027-03-01",
    linkedFacilityCodes: ["KMK-001"]
  };
}

function blankTbo() {
  return {
    tboId: "TBO-001",
    linkedFacilityCode: "KMK-001",
    tboCategory: "SYARAT_PENCAIRAN",
    documentRequirement: "Bukti lunas pajak tahun terakhir",
    dueDate: "2026-05-10",
    status: "PENDING",
    pic: "RM"
  };
}

function blankCovenant() {
  return {
    covenantId: "COV-001",
    covenantType: "FINANCIAL",
    description: "Nasabah wajib menjaga Current Ratio > 1.0x",
    testingFrequency: "QUARTERLY",
    measurementDate: "2026-06-30",
    status: "PENDING",
    penaltyDetails: "Warning letter and repricing"
  };
}

export default function App() {
  const [activeTab, setActiveTab] = useState("portfolio");
  const [loading, setLoading] = useState(true);
  const [feedback, setFeedback] = useState(null);
  const [dashboard, setDashboard] = useState(null);
  const [customers, setCustomers] = useState([]);
  const [applications, setApplications] = useState([]);
  const [tasks, setTasks] = useState([]);
  const [rules, setRules] = useState([]);
  const [selectedApplicationId, setSelectedApplicationId] = useState("");
  const [customerForm, setCustomerForm] = useState(createCustomerForm());
  const [applicationForm, setApplicationForm] = useState(createApplicationForm());
  const [ruleForm, setRuleForm] = useState(createRuleForm());
  const [taskForms, setTaskForms] = useState({});

  const selectedApplication = useMemo(
    () => applications.find((item) => item.applicationId === selectedApplicationId) ?? null,
    [applications, selectedApplicationId]
  );

  useEffect(() => {
    refreshAll();
  }, []);

  async function refreshAll() {
    try {
      setLoading(true);
      const [dashboardData, customerData, applicationData, taskData, ruleData] = await Promise.all([
        api("/api/dashboard/summary"),
        api("/api/customers"),
        api("/api/applications"),
        api("/api/approvals/tasks"),
        api("/api/rules")
      ]);
      setDashboard(dashboardData);
      setCustomers(customerData);
      setApplications(applicationData);
      setTasks(taskData);
      setRules(ruleData);
      setSelectedApplicationId((current) => current || applicationData[0]?.applicationId || "");
    } catch (error) {
      notify(error.message, "error");
    } finally {
      setLoading(false);
    }
  }

  async function saveCustomer(event) {
    event.preventDefault();
    try {
      await api("/api/customers", { method: "POST", body: JSON.stringify(customerPayload(customerForm)) });
      setCustomerForm(createCustomerForm());
      notify("Customer portfolio saved.", "success");
      await refreshAll();
    } catch (error) {
      notify(error.message, "error");
    }
  }

  async function saveApplication(event) {
    event.preventDefault();
    try {
      const saved = await api("/api/applications", { method: "POST", body: JSON.stringify(applicationPayload(applicationForm)) });
      setApplicationForm((current) => ({ ...current, applicationId: saved.applicationId }));
      setSelectedApplicationId(saved.applicationId);
      notify(`Draft ${saved.applicationId} saved.`, "success");
      await refreshAll();
    } catch (error) {
      notify(error.message, "error");
    }
  }

  async function submitApplication(applicationId) {
    try {
      await api(`/api/applications/${applicationId}/submit`, { method: "POST" });
      notify(`Application ${applicationId} submitted to Flowable.`, "success");
      await refreshAll();
      setActiveTab("tasks");
    } catch (error) {
      notify(error.message, "error");
    }
  }

  async function saveRule(event) {
    event.preventDefault();
    try {
      await api("/api/rules", { method: "POST", body: JSON.stringify(rulePayload(ruleForm)) });
      setRuleForm(createRuleForm());
      notify("Rule saved.", "success");
      await refreshAll();
    } catch (error) {
      notify(error.message, "error");
    }
  }

  async function completeTask(taskId) {
    try {
      await api(`/api/approvals/tasks/${taskId}/complete`, {
        method: "POST",
        body: JSON.stringify(taskForms[taskId] || { decision: "APPROVE", decidedBy: "", decisionNotes: "" })
      });
      notify(`Task ${taskId} completed.`, "success");
      await refreshAll();
    } catch (error) {
      notify(error.message, "error");
    }
  }

  function notify(message, type) {
    setFeedback({ message, type });
    window.clearTimeout(notify.timeoutId);
    notify.timeoutId = window.setTimeout(() => setFeedback(null), 4500);
  }

  return (
    <div className="app-shell">
      <div className="background-layer background-layer-a" />
      <div className="background-layer background-layer-b" />

      <header className="hero">
        <div>
          <p className="eyebrow">Flowable Approval Workflow</p>
          <h1>ULOS Corporate Lending Workspace</h1>
          <p className="hero-copy">
            Customer 360, corporate application, collateral governance, deviation rule, and approval routing in a single React cockpit.
          </p>
        </div>
        <div className="tab-bar">
          {tabs.map((tab) => (
            <button key={tab.key} type="button" className={`tab-chip ${activeTab === tab.key ? "active" : ""}`} onClick={() => setActiveTab(tab.key)}>
              {tab.label}
            </button>
          ))}
        </div>
      </header>

      {feedback ? <Notice type={feedback.type} message={feedback.message} /> : null}

      {loading ? (
        <section className="panel loading-panel">Loading ULOS cockpit...</section>
      ) : (
        <>
          <section className="metrics-grid">
            <MetricCard label="Customers" value={dashboard?.totalCustomers ?? 0} tone="amber" />
            <MetricCard label="Applications" value={dashboard?.totalApplications ?? 0} tone="navy" />
            <MetricCard label="In Review" value={dashboard?.inReviewApplications ?? 0} tone="green" />
            <MetricCard label="Open Tasks" value={dashboard?.openTasks ?? 0} tone="rose" />
            <MetricCard label="Proposed Exposure" value={formatCurrency(dashboard?.totalProposedExposure)} tone="slate" />
          </section>

          <main className="page-grid">
            {activeTab === "portfolio" ? (
              <>
                <section className="panel">
                  <PanelHeader title="Customer / Portfolio Management" description="Gunakan field standar untuk identitas utama dan JSON editor untuk data berulang seperti manajemen, shareholder, laporan keuangan, rekening koran, dan business operation." />
                  <form className="stacked-form" onSubmit={saveCustomer}>
                    <div className="field-grid three">
                      <Field label="CIF Number" value={customerForm.cifNumber} onChange={(value) => setFormField(setCustomerForm, "cifNumber", value)} />
                      <Field label="Company Name" value={customerForm.companyName} onChange={(value) => setFormField(setCustomerForm, "companyName", value)} />
                      <Field label="Company Type" value={customerForm.companyType} onChange={(value) => setFormField(setCustomerForm, "companyType", value)} as="select" options={companyTypes} />
                    </div>
                    <div className="field-grid four">
                      <Field label="Date of Establishment" type="date" value={customerForm.dateOfEstablishment} onChange={(value) => setFormField(setCustomerForm, "dateOfEstablishment", value)} />
                      <Field label="Place of Establishment" value={customerForm.placeOfEstablishment} onChange={(value) => setFormField(setCustomerForm, "placeOfEstablishment", value)} />
                      <Field label="NPWP" value={customerForm.taxId} onChange={(value) => setFormField(setCustomerForm, "taxId", value)} />
                      <Field label="NIB / SIUP / TDP" value={customerForm.businessLicense} onChange={(value) => setFormField(setCustomerForm, "businessLicense", value)} />
                    </div>
                    <TextAreaField label="Office Address" value={customerForm.officeAddress} onChange={(value) => setFormField(setCustomerForm, "officeAddress", value)} />
                    <TextAreaField label="Factory / Operation Address" value={customerForm.factoryAddress} onChange={(value) => setFormField(setCustomerForm, "factoryAddress", value)} />
                    <JsonEditor label="Key Management" value={customerForm.keyManagementJson} onChange={(value) => setFormField(setCustomerForm, "keyManagementJson", value)} />
                    <JsonEditor label="Shareholder Structure" value={customerForm.shareholdersJson} onChange={(value) => setFormField(setCustomerForm, "shareholdersJson", value)} />
                    <JsonEditor label="Related / Collateral Provider" value={customerForm.relatedPartiesJson} onChange={(value) => setFormField(setCustomerForm, "relatedPartiesJson", value)} />
                    <JsonEditor label="Financial Statement (Stand-alone)" value={customerForm.financialStatementsJson} onChange={(value) => setFormField(setCustomerForm, "financialStatementsJson", value)} />
                    <JsonEditor label="Financial Statement (Group / Consolidated)" value={customerForm.consolidatedFinancialStatementsJson} onChange={(value) => setFormField(setCustomerForm, "consolidatedFinancialStatementsJson", value)} />
                    <JsonEditor label="Bank Statement" value={customerForm.bankStatementsJson} onChange={(value) => setFormField(setCustomerForm, "bankStatementsJson", value)} />
                    <JsonEditor label="Supplier" value={customerForm.suppliersJson} onChange={(value) => setFormField(setCustomerForm, "suppliersJson", value)} />
                    <JsonEditor label="Buyer / Customer" value={customerForm.buyersJson} onChange={(value) => setFormField(setCustomerForm, "buyersJson", value)} />
                    <JsonEditor label="Competitor" value={customerForm.competitorsJson} onChange={(value) => setFormField(setCustomerForm, "competitorsJson", value)} />
                    <div className="form-actions">
                      <button className="primary-button" type="submit">Save Customer 360</button>
                    </div>
                  </form>
                </section>

                <section className="panel">
                  <PanelHeader title="Portfolio Book" description="Company master list yang sudah masuk ke LOS dan siap dipakai sebagai dasar pengajuan kredit baru." />
                  <div className="card-stack">
                    {customers.length === 0 ? (
                      <EmptyState title="Belum ada customer" body="Simpan customer pertama dari panel kiri." />
                    ) : (
                      customers.map((customer) => (
                        <article className="entity-card" key={customer.cifNumber}>
                          <div>
                            <p className="card-tag">{customer.companyType}</p>
                            <h3>{customer.companyName}</h3>
                            <p className="card-caption">CIF {customer.cifNumber}</p>
                          </div>
                          <button type="button" className="ghost-button" onClick={() => { setFormField(setApplicationForm, "customerCif", customer.cifNumber); setActiveTab("application"); }}>
                            Create Application
                          </button>
                        </article>
                      ))
                    )}
                  </div>
                </section>
              </>
            ) : null}

            {activeTab === "application" ? (
              <>
                <section className="panel">
                  <PanelHeader title="Application Management" description="Simpan draft aplikasi, hitung exposure dan BMPK, lalu submit ke workflow approval Flowable." />
                  <form className="stacked-form" onSubmit={saveApplication}>
                    <div className="field-grid four">
                      <Field label="Application ID" value={applicationForm.applicationId} onChange={(value) => setFormField(setApplicationForm, "applicationId", value)} placeholder="Auto-generated" />
                      <Field label="Customer" value={applicationForm.customerCif} onChange={(value) => setFormField(setApplicationForm, "customerCif", value)} as="select" options={customers.map((item) => ({ label: `${item.companyName} (${item.cifNumber})`, value: item.cifNumber }))} />
                      <Field label="Application Date" type="date" value={applicationForm.applicationDate} onChange={(value) => setFormField(setApplicationForm, "applicationDate", value)} />
                      <Field label="Application Type" value={applicationForm.applicationType} onChange={(value) => setFormField(setApplicationForm, "applicationType", value)} as="select" options={applicationTypes} />
                    </div>
                    <div className="field-grid four">
                      <Field label="RM / AO" value={applicationForm.rmName} onChange={(value) => setFormField(setApplicationForm, "rmName", value)} />
                      <Field label="Branch" value={applicationForm.branchName} onChange={(value) => setFormField(setApplicationForm, "branchName", value)} />
                      <Field label="Region" value={applicationForm.regionName} onChange={(value) => setFormField(setApplicationForm, "regionName", value)} />
                      <Field label="CBC" value={applicationForm.cbcName} onChange={(value) => setFormField(setApplicationForm, "cbcName", value)} />
                    </div>
                    <div className="field-grid four">
                      <Field label="Group Relationship" value={applicationForm.groupRelationshipStatus} onChange={(value) => setFormField(setApplicationForm, "groupRelationshipStatus", value)} />
                      <Field label="Core Capital Bank" type="number" value={applicationForm.coreCapitalBank} onChange={(value) => setFormField(setApplicationForm, "coreCapitalBank", value)} />
                      <Field label="Max Lending Limit %" type="number" value={applicationForm.maxLendingLimitPercentage} onChange={(value) => setFormField(setApplicationForm, "maxLendingLimitPercentage", value)} />
                      <Field label="Existing Exposure Group" type="number" value={applicationForm.existingExposureGroup} onChange={(value) => setFormField(setApplicationForm, "existingExposureGroup", value)} />
                    </div>
                    <div className="field-grid four">
                      <Field label="BI Sector Code" value={applicationForm.biSectorCode} onChange={(value) => setFormField(setApplicationForm, "biSectorCode", value)} />
                      <Field label="Sub-sector" value={applicationForm.subSectorDescription} onChange={(value) => setFormField(setApplicationForm, "subSectorDescription", value)} />
                      <Field label="Industry Outlook" value={applicationForm.industryOutlook} onChange={(value) => setFormField(setApplicationForm, "industryOutlook", value)} as="select" options={industryOutlooks} />
                      <Field label="ESG / Green Financing" value={applicationForm.esgGreenFinancingStatus} onChange={(value) => setFormField(setApplicationForm, "esgGreenFinancingStatus", value)} />
                    </div>
                    <TextAreaField label="Justification Note" value={applicationForm.justificationNote} onChange={(value) => setFormField(setApplicationForm, "justificationNote", value)} />
                    <JsonEditor label="Facilities JSON" value={applicationForm.facilitiesJson} onChange={(value) => setFormField(setApplicationForm, "facilitiesJson", value)} />
                    <JsonEditor label="Collaterals JSON" value={applicationForm.collateralsJson} onChange={(value) => setFormField(setApplicationForm, "collateralsJson", value)} />
                    <JsonEditor label="TBO JSON" value={applicationForm.tboItemsJson} onChange={(value) => setFormField(setApplicationForm, "tboItemsJson", value)} />
                    <JsonEditor label="Covenants JSON" value={applicationForm.covenantsJson} onChange={(value) => setFormField(setApplicationForm, "covenantsJson", value)} />
                    <div className="form-actions split">
                      <button className="primary-button" type="submit">Save Draft</button>
                      {applicationForm.applicationId ? (
                        <button className="secondary-button" type="button" onClick={() => submitApplication(applicationForm.applicationId)}>Submit to Flowable</button>
                      ) : null}
                    </div>
                  </form>
                </section>

                <section className="panel">
                  <PanelHeader title="Application Board" description="Pilih draft atau aplikasi yang sudah berjalan untuk melihat exposure, hasil rule engine, dan histori approval." />
                  <div className="card-stack">
                    {applications.length === 0 ? (
                      <EmptyState title="Belum ada aplikasi" body="Simpan draft dulu untuk mulai approval route." />
                    ) : (
                      applications.map((application) => (
                        <button key={application.applicationId} type="button" className={`application-card ${selectedApplicationId === application.applicationId ? "selected" : ""}`} onClick={() => setSelectedApplicationId(application.applicationId)}>
                          <div className="application-card-head">
                            <div>
                              <p className="card-tag">{application.workflowStatus}</p>
                              <h3>{application.customer.companyName}</h3>
                            </div>
                            <span className="pill">{application.applicationType}</span>
                          </div>
                          <p className="card-caption">{application.applicationId}</p>
                          <div className="mini-metrics">
                            <span>{formatCurrency(application.proposedExposure)}</span>
                            <span>{application.currentApprovalTier}</span>
                          </div>
                        </button>
                      ))
                    )}
                  </div>
                  {selectedApplication ? <ApplicationDetail application={selectedApplication} onSubmit={submitApplication} /> : null}
                </section>
              </>
            ) : null}

            {activeTab === "tasks" ? (
              <>
                <section className="panel">
                  <PanelHeader title="Approval Inbox" description="Task aktif dari Flowable untuk analyst, branch manager, regional head, committee, atau board." />
                  <div className="card-stack">
                    {tasks.length === 0 ? (
                      <EmptyState title="Inbox kosong" body="Tidak ada task approval aktif." />
                    ) : (
                      tasks.map((task) => (
                        <article className="task-card" key={task.taskId}>
                          <div className="application-card-head">
                            <div>
                              <p className="card-tag">{task.approvalTier}</p>
                              <h3>{task.companyName}</h3>
                              <p className="card-caption">{task.taskName} - {task.applicationId}</p>
                            </div>
                            <span className="pill">{formatCurrency(task.proposedExposure)}</span>
                          </div>
                          <div className="field-grid three">
                            <Field label="Decision" value={taskForms[task.taskId]?.decision || "APPROVE"} onChange={(value) => setTaskField(setTaskForms, task.taskId, "decision", value)} as="select" options={["APPROVE", "REJECT"]} />
                            <Field label="Decided By" value={taskForms[task.taskId]?.decidedBy || task.assignee || ""} onChange={(value) => setTaskField(setTaskForms, task.taskId, "decidedBy", value)} />
                            <Field label="Assignee" value={task.assignee || "-"} onChange={() => {}} disabled />
                          </div>
                          <TextAreaField label="Decision Notes" value={taskForms[task.taskId]?.decisionNotes || ""} onChange={(value) => setTaskField(setTaskForms, task.taskId, "decisionNotes", value)} />
                          <div className="form-actions split">
                            <button className="ghost-button" type="button" onClick={() => { setSelectedApplicationId(task.applicationId); setActiveTab("application"); }}>View Application</button>
                            <button className="primary-button" type="button" onClick={() => completeTask(task.taskId)}>Complete Task</button>
                          </div>
                        </article>
                      ))
                    )}
                  </div>
                </section>

                <section className="panel">
                  <PanelHeader title="Queue Snapshot" description="Ringkasan portofolio aplikasi yang sedang menunggu keputusan." />
                  <div className="detail-grid">
                    <DetailRow label="Draft" value={dashboard?.draftApplications ?? 0} />
                    <DetailRow label="In Review" value={dashboard?.inReviewApplications ?? 0} />
                    <DetailRow label="Approved" value={dashboard?.approvedApplications ?? 0} />
                    <DetailRow label="Rejected" value={dashboard?.rejectedApplications ?? 0} />
                    <DetailRow label="Hard Stop" value={dashboard?.hardStopApplications ?? 0} />
                    <DetailRow label="Pending TBO" value={dashboard?.pendingTboCount ?? 0} />
                  </div>
                </section>
              </>
            ) : null}

            {activeTab === "rules" ? (
              <>
                <section className="panel">
                  <PanelHeader title="Business Rule Management" description="Kelola hard stop, warning, dan deviation beserta tier approval yang dihasilkan." />
                  <form className="stacked-form" onSubmit={saveRule}>
                    <div className="field-grid two">
                      <Field label="Rule ID" value={ruleForm.ruleId} onChange={(value) => setFormField(setRuleForm, "ruleId", value)} />
                      <Field label="Rule Name" value={ruleForm.ruleName} onChange={(value) => setFormField(setRuleForm, "ruleName", value)} />
                    </div>
                    <div className="field-grid four">
                      <Field label="Rule Type" value={ruleForm.ruleType} onChange={(value) => setFormField(setRuleForm, "ruleType", value)} as="select" options={ruleTypes} />
                      <Field label="Metric Key" value={ruleForm.metricKey} onChange={(value) => setFormField(setRuleForm, "metricKey", value)} as="select" options={metricKeys} />
                      <Field label="Operator" value={ruleForm.operator} onChange={(value) => setFormField(setRuleForm, "operator", value)} as="select" options={operators} />
                      <Field label="Threshold Value" type="number" value={ruleForm.thresholdValue} onChange={(value) => setFormField(setRuleForm, "thresholdValue", value)} />
                    </div>
                    <div className="field-grid four">
                      <Field label="Routing" value={ruleForm.actionRouting} onChange={(value) => setFormField(setRuleForm, "actionRouting", value)} as="select" options={routingTypes} />
                      <Field label="Base Approver" value={ruleForm.baseApprover} onChange={(value) => setFormField(setRuleForm, "baseApprover", value)} />
                      <Field label="Escalated Approver" value={ruleForm.escalatedApprover} onChange={(value) => setFormField(setRuleForm, "escalatedApprover", value)} />
                      <Field label="Committee Approver" value={ruleForm.committeeApprover} onChange={(value) => setFormField(setRuleForm, "committeeApprover", value)} />
                    </div>
                    <div className="field-grid three">
                      <Field label="Board Approver" value={ruleForm.boardApprover} onChange={(value) => setFormField(setRuleForm, "boardApprover", value)} />
                      <Field label="Justification Required" value={ruleForm.justificationRequired} onChange={(value) => setFormField(setRuleForm, "justificationRequired", value)} as="select" options={["true", "false"]} />
                      <Field label="Active" value={ruleForm.active} onChange={(value) => setFormField(setRuleForm, "active", value)} as="select" options={["true", "false"]} />
                    </div>
                    <TextAreaField label="Condition / Parameter" value={ruleForm.conditionExpression} onChange={(value) => setFormField(setRuleForm, "conditionExpression", value)} />
                    <TextAreaField label="Message Template" value={ruleForm.messageTemplate} onChange={(value) => setFormField(setRuleForm, "messageTemplate", value)} />
                    <div className="form-actions">
                      <button className="primary-button" type="submit">Save Rule</button>
                    </div>
                  </form>
                </section>

                <section className="panel">
                  <PanelHeader title="Policy Catalogue" description="Rule aktif yang saat ini dibaca oleh approval engine di backend." />
                  <div className="rule-table">
                    <div className="rule-row rule-head"><span>ID</span><span>Name</span><span>Type</span><span>Condition</span><span>Routing</span></div>
                    {rules.map((rule) => (
                      <div className="rule-row" key={rule.ruleId}>
                        <span>{rule.ruleId}</span>
                        <span>{rule.ruleName}</span>
                        <span>{rule.ruleType}</span>
                        <span>{rule.conditionExpression}</span>
                        <span>{rule.actionRouting}</span>
                      </div>
                    ))}
                  </div>
                </section>
              </>
            ) : null}
          </main>
        </>
      )}
    </div>
  );
}

function sampleJson(value) {
  return JSON.stringify(value, null, 2);
}

function parseJson(label, value) {
  try {
    return JSON.parse(value || "[]");
  } catch (error) {
    throw new Error(`${label} JSON tidak valid`);
  }
}

function customerPayload(form) {
  return sanitize({
    ...form,
    keyManagement: parseJson("Key management", form.keyManagementJson),
    shareholders: parseJson("Shareholders", form.shareholdersJson),
    relatedParties: parseJson("Related parties", form.relatedPartiesJson),
    financialStatements: parseJson("Financial statements", form.financialStatementsJson),
    consolidatedFinancialStatements: parseJson("Consolidated financial statements", form.consolidatedFinancialStatementsJson),
    bankStatements: parseJson("Bank statements", form.bankStatementsJson),
    suppliers: parseJson("Suppliers", form.suppliersJson),
    buyers: parseJson("Buyers", form.buyersJson),
    competitors: parseJson("Competitors", form.competitorsJson)
  });
}

function applicationPayload(form) {
  return sanitize({
    ...form,
    facilities: parseJson("Facilities", form.facilitiesJson),
    collaterals: parseJson("Collaterals", form.collateralsJson),
    tboItems: parseJson("TBO items", form.tboItemsJson),
    covenants: parseJson("Covenants", form.covenantsJson)
  });
}

function rulePayload(form) {
  return sanitize({
    ...form,
    justificationRequired: form.justificationRequired === "true",
    active: form.active === "true"
  });
}

function sanitize(value) {
  if (Array.isArray(value)) {
    return value.map((item) => sanitize(item));
  }
  if (value && typeof value === "object") {
    return Object.fromEntries(Object.entries(value).map(([key, nested]) => [key, sanitize(nested)]));
  }
  return value === "" ? null : value;
}

async function api(path, options = {}) {
  const response = await fetch(path, {
    headers: { "Content-Type": "application/json" },
    ...options
  });
  if (!response.ok) {
    throw new Error((await response.text()) || `Request failed: ${response.status}`);
  }
  if (response.status === 204) {
    return null;
  }
  return response.json();
}

function formatCurrency(value) {
  if (value === null || value === undefined || value === "") return "-";
  return currencyFormatter.format(Number(value));
}

function formatMetric(value) {
  if (value === null || value === undefined || value === "") return "-";
  return Number(value).toFixed(2);
}

function setFormField(setter, key, value) {
  setter((current) => ({ ...current, [key]: value }));
}

function setTaskField(setter, taskId, key, value) {
  setter((current) => ({
    ...current,
    [taskId]: {
      decision: "APPROVE",
      decidedBy: "",
      decisionNotes: "",
      ...current[taskId],
      [key]: value
    }
  }));
}

function Notice({ type, message }) {
  return <div className={`notice ${type}`}>{message}</div>;
}

function PanelHeader({ title, description }) {
  return (
    <div className="panel-header">
      <div>
        <p className="eyebrow subtle">Operational Module</p>
        <h2>{title}</h2>
      </div>
      <p className="panel-copy">{description}</p>
    </div>
  );
}

function MetricCard({ label, value, tone }) {
  return (
    <article className={`metric-card ${tone}`}>
      <span>{label}</span>
      <strong>{value}</strong>
    </article>
  );
}

function Field({ label, value, onChange, as = "input", type = "text", options = [], disabled = false, placeholder = "" }) {
  const resolved = options.map((item) => (typeof item === "string" ? { label: item, value: item } : item));
  return (
    <label className="field">
      <span>{label}</span>
      {as === "select" ? (
        <select value={value ?? ""} onChange={(event) => onChange(event.target.value)} disabled={disabled}>
          <option value="">Select</option>
          {resolved.map((option) => (
            <option key={option.value} value={option.value}>
              {option.label}
            </option>
          ))}
        </select>
      ) : (
        <input type={type} value={value ?? ""} onChange={(event) => onChange(event.target.value)} disabled={disabled} placeholder={placeholder} />
      )}
    </label>
  );
}

function TextAreaField({ label, value, onChange }) {
  return (
    <label className="field">
      <span>{label}</span>
      <textarea rows={3} value={value ?? ""} onChange={(event) => onChange(event.target.value)} />
    </label>
  );
}

function JsonEditor({ label, value, onChange }) {
  return (
    <label className="field json-field">
      <span>{label}</span>
      <textarea rows={10} value={value ?? ""} onChange={(event) => onChange(event.target.value)} spellCheck={false} />
    </label>
  );
}

function EmptyState({ title, body }) {
  return (
    <div className="empty-state">
      <h3>{title}</h3>
      <p>{body}</p>
    </div>
  );
}

function DetailRow({ label, value }) {
  return (
    <div className="detail-row">
      <span>{label}</span>
      <strong>{value}</strong>
    </div>
  );
}

function ApplicationDetail({ application, onSubmit }) {
  return (
    <div className="detail-sheet">
      <div className="application-card-head">
        <div>
          <h3>{application.applicationId}</h3>
          <p className="card-caption">{application.customer.companyName} - {application.workflowStatus}</p>
        </div>
        {application.workflowStatus === "DRAFT" ? (
          <button type="button" className="primary-button" onClick={() => onSubmit(application.applicationId)}>
            Submit
          </button>
        ) : null}
      </div>

      <div className="detail-grid">
        <DetailRow label="Proposed Exposure" value={formatCurrency(application.proposedExposure)} />
        <DetailRow label="Available Limit" value={formatCurrency(application.availableLimit)} />
        <DetailRow label="Collateral Coverage" value={`${formatMetric(application.collateralCoverage)}%`} />
        <DetailRow label="Current Ratio" value={formatMetric(application.latestStandaloneAnalysis?.currentRatio)} />
        <DetailRow label="DER" value={formatMetric(application.latestStandaloneAnalysis?.debtToEquityRatio)} />
        <DetailRow label="Approval Tier" value={application.currentApprovalTier} />
      </div>

      <section className="sub-panel">
        <h4>Triggered Rules</h4>
        {application.ruleEvaluation?.hits?.length ? (
          <div className="badge-flow">
            {application.ruleEvaluation.hits.map((hit) => (
              <span className="pill accent" key={hit.ruleId}>{hit.ruleName}</span>
            ))}
          </div>
        ) : (
          <p className="muted-copy">No rule hits on current snapshot.</p>
        )}
      </section>

      <section className="sub-panel">
        <h4>Approval History</h4>
        {application.approvalHistory?.length ? (
          <div className="history-list">
            {application.approvalHistory.map((item, index) => (
              <div className="history-row" key={`${item.stage}-${index}`}>
                <strong>{item.stage}</strong>
                <span>{item.decision}</span>
                <span>{item.actor}</span>
                <span>{item.notes || "-"}</span>
              </div>
            ))}
          </div>
        ) : (
          <p className="muted-copy">No approval history yet.</p>
        )}
      </section>
    </div>
  );
}
