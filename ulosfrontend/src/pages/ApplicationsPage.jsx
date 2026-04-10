import { useEffect, useState } from "react";
import { api } from "../lib/api";
import { JsonField, Notice, Panel, Field, TextAreaField, EmptyState, setField } from "../components/ui";
import { formatCurrency, formatMetric, parseJson, sampleJson, sanitize } from "../lib/forms";

const applicationTypes = ["NEW", "RENEWAL", "TOP_UP", "RESTRUCTURING", "TAKE_OVER"];
const outlooks = ["POSITIVE", "STABLE", "NEGATIVE"];

function createForm(user) {
  return {
    applicationId: "",
    customerCif: "",
    applicationDate: new Date().toISOString().slice(0, 10),
    applicationType: "NEW",
    rmName: user?.username || "rm.user",
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
    facilitiesJson: sampleJson([]),
    collateralsJson: sampleJson([]),
    tboItemsJson: sampleJson([]),
    covenantsJson: sampleJson([])
  };
}

export default function ApplicationsPage({ user }) {
  const [form, setForm] = useState(() => createForm(user));
  const [customers, setCustomers] = useState([]);
  const [applications, setApplications] = useState([]);
  const [selectedApplication, setSelectedApplication] = useState(null);
  const [message, setMessage] = useState(null);

  useEffect(() => {
    refresh();
  }, []);

  async function refresh(selectedId) {
    try {
      const [customerList, applicationList] = await Promise.all([api("/api/customers"), api("/api/applications")]);
      setCustomers(customerList);
      setApplications(applicationList);
      const applicationId = selectedId || applicationList[0]?.applicationId;
      if (applicationId) {
        setSelectedApplication(await api(`/api/applications/${applicationId}`));
      } else {
        setSelectedApplication(null);
      }
    } catch (error) {
      setMessage({ type: "error", text: error.message });
    }
  }

  async function handleSaveDraft(event) {
    event.preventDefault();
    try {
      const payload = sanitize({
        ...form,
        facilities: parseJson("Facilities", form.facilitiesJson),
        collaterals: parseJson("Collaterals", form.collateralsJson),
        tboItems: parseJson("TBO", form.tboItemsJson),
        covenants: parseJson("Covenants", form.covenantsJson)
      });
      const saved = await api("/api/applications", { method: "POST", body: JSON.stringify(payload) });
      setForm((current) => ({ ...current, applicationId: saved.applicationId }));
      setMessage({ type: "success", text: `Draft ${saved.applicationId} saved.` });
      await refresh(saved.applicationId);
    } catch (error) {
      setMessage({ type: "error", text: error.message });
    }
  }

  async function handleSubmit(applicationId) {
    try {
      await api(`/api/applications/${applicationId}/submit`, { method: "POST" });
      setMessage({ type: "success", text: `Application ${applicationId} submitted to workflow.` });
      await refresh(applicationId);
    } catch (error) {
      setMessage({ type: "error", text: error.message });
    }
  }

  return (
    <div className="page-stack">
      <Notice type={message?.type} message={message?.text} />
      <div className="two-column">
        <Panel title="Application Management" description="Capture BMPK, facility, collateral, TBO, covenant, lalu submit ke workflow approval.">
          <form className="stacked-form" onSubmit={handleSaveDraft}>
            <div className="field-grid four">
              <Field label="Application ID" value={form.applicationId} onChange={(value) => setField(setForm, "applicationId", value)} placeholder="Auto-generated" />
              <Field label="Customer" value={form.customerCif} onChange={(value) => setField(setForm, "customerCif", value)} as="select" options={customers.map((item) => ({ label: `${item.companyName} (${item.cifNumber})`, value: item.cifNumber }))} />
              <Field label="Application Date" type="date" value={form.applicationDate} onChange={(value) => setField(setForm, "applicationDate", value)} />
              <Field label="Application Type" value={form.applicationType} onChange={(value) => setField(setForm, "applicationType", value)} as="select" options={applicationTypes} />
            </div>
            <div className="field-grid four">
              <Field label="RM Username" value={form.rmName} onChange={(value) => setField(setForm, "rmName", value)} />
              <Field label="Branch" value={form.branchName} onChange={(value) => setField(setForm, "branchName", value)} />
              <Field label="Region" value={form.regionName} onChange={(value) => setField(setForm, "regionName", value)} />
              <Field label="CBC" value={form.cbcName} onChange={(value) => setField(setForm, "cbcName", value)} />
            </div>
            <div className="field-grid four">
              <Field label="Group Relationship" value={form.groupRelationshipStatus} onChange={(value) => setField(setForm, "groupRelationshipStatus", value)} />
              <Field label="Core Capital Bank" type="number" value={form.coreCapitalBank} onChange={(value) => setField(setForm, "coreCapitalBank", value)} />
              <Field label="Max Lending Limit %" type="number" value={form.maxLendingLimitPercentage} onChange={(value) => setField(setForm, "maxLendingLimitPercentage", value)} />
              <Field label="Existing Exposure Group" type="number" value={form.existingExposureGroup} onChange={(value) => setField(setForm, "existingExposureGroup", value)} />
            </div>
            <div className="field-grid four">
              <Field label="BI Sector Code" value={form.biSectorCode} onChange={(value) => setField(setForm, "biSectorCode", value)} />
              <Field label="Sub-Sector" value={form.subSectorDescription} onChange={(value) => setField(setForm, "subSectorDescription", value)} />
              <Field label="Industry Outlook" value={form.industryOutlook} onChange={(value) => setField(setForm, "industryOutlook", value)} as="select" options={outlooks} />
              <Field label="ESG / Green Financing" value={form.esgGreenFinancingStatus} onChange={(value) => setField(setForm, "esgGreenFinancingStatus", value)} />
            </div>
            <TextAreaField label="Justification Note" value={form.justificationNote} onChange={(value) => setField(setForm, "justificationNote", value)} />
            <JsonField label="Facilities JSON" value={form.facilitiesJson} onChange={(value) => setField(setForm, "facilitiesJson", value)} />
            <JsonField label="Collaterals JSON" value={form.collateralsJson} onChange={(value) => setField(setForm, "collateralsJson", value)} />
            <JsonField label="TBO JSON" value={form.tboItemsJson} onChange={(value) => setField(setForm, "tboItemsJson", value)} />
            <JsonField label="Covenants JSON" value={form.covenantsJson} onChange={(value) => setField(setForm, "covenantsJson", value)} />
            <div className="action-row">
              <button type="submit" className="primary-button">Save Draft</button>
              {form.applicationId ? (
                <button type="button" className="secondary-button" onClick={() => handleSubmit(form.applicationId)}>Submit Workflow</button>
              ) : null}
            </div>
          </form>
        </Panel>

        <Panel title="Application Board" description="Status pengajuan, exposure, rule engine result, dan approval history.">
          <div className="card-stack">
            {applications.length === 0 ? (
              <EmptyState title="Belum ada aplikasi" body="Buat draft pertama pada panel kiri." />
            ) : (
              applications.map((application) => (
                <button key={application.applicationId} type="button" className={`list-card ${selectedApplication?.applicationId === application.applicationId ? "active" : ""}`} onClick={() => refresh(application.applicationId)}>
                  <div>
                    <p className="eyebrow subtle">{application.workflowStatus}</p>
                    <h3>{application.customer.companyName}</h3>
                    <p className="panel-copy">{application.applicationId}</p>
                  </div>
                  <span className="badge">{formatCurrency(application.proposedExposure)}</span>
                </button>
              ))
            )}
          </div>

          {selectedApplication ? (
            <div className="detail-sheet">
              <div className="action-row">
                <h3>{selectedApplication.applicationId}</h3>
                {selectedApplication.workflowStatus === "DRAFT" ? (
                  <button type="button" className="primary-button" onClick={() => handleSubmit(selectedApplication.applicationId)}>Submit</button>
                ) : null}
              </div>
              <div className="detail-grid">
                <Detail label="Exposure" value={formatCurrency(selectedApplication.proposedExposure)} />
                <Detail label="Available Limit" value={formatCurrency(selectedApplication.availableLimit)} />
                <Detail label="Collateral Coverage" value={`${formatMetric(selectedApplication.collateralCoverage)}%`} />
                <Detail label="Current Ratio" value={formatMetric(selectedApplication.latestStandaloneAnalysis?.currentRatio)} />
                <Detail label="DER" value={formatMetric(selectedApplication.latestStandaloneAnalysis?.debtToEquityRatio)} />
                <Detail label="Tier" value={selectedApplication.currentApprovalTier} />
              </div>
              <div className="sub-panel">
                <h4>Triggered Rules</h4>
                <div className="badge-stack">
                  {(selectedApplication.ruleEvaluation?.hits || []).map((hit) => (
                    <span key={hit.ruleId} className="badge accent">{hit.ruleName}</span>
                  ))}
                  {(selectedApplication.ruleEvaluation?.hits || []).length === 0 ? <span className="panel-copy">No rule hits.</span> : null}
                </div>
              </div>
            </div>
          ) : null}
        </Panel>
      </div>
    </div>
  );
}

function Detail({ label, value }) {
  return (
    <div className="detail-card">
      <span>{label}</span>
      <strong>{value}</strong>
    </div>
  );
}
