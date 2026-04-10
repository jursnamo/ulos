import { useEffect, useState } from "react";
import { api } from "../lib/api";
import { Field, Notice, Panel, TextAreaField, setField } from "../components/ui";
import { sanitize } from "../lib/forms";

const ruleTypes = ["HARD_STOP", "WARNING", "DEVIATION"];
const metrics = ["AVAILABLE_LIMIT", "PROPOSED_EXPOSURE", "DER", "COLLATERAL_COVERAGE", "CURRENT_RATIO"];
const operators = ["GT", "GTE", "LT", "LTE", "EQ"];
const routings = ["BRANCH_MANAGER", "REGIONAL_HEAD", "CREDIT_COMMITTEE", "BOARD_OF_DIRECTORS"];

function createForm() {
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

export default function RulesPage() {
  const [form, setForm] = useState(createForm());
  const [rules, setRules] = useState([]);
  const [message, setMessage] = useState(null);

  useEffect(() => {
    loadRules();
  }, []);

  async function loadRules() {
    try {
      setRules(await api("/api/rules"));
    } catch (error) {
      setMessage({ type: "error", text: error.message });
    }
  }

  async function handleSubmit(event) {
    event.preventDefault();
    try {
      await api("/api/rules", {
        method: "POST",
        body: JSON.stringify(sanitize({
          ...form,
          justificationRequired: form.justificationRequired === "true",
          active: form.active === "true"
        }))
      });
      setForm(createForm());
      setMessage({ type: "success", text: "Rule saved." });
      await loadRules();
    } catch (error) {
      setMessage({ type: "error", text: error.message });
    }
  }

  return (
    <div className="page-stack">
      <Notice type={message?.type} message={message?.text} />
      <div className="two-column">
        <Panel title="Business Rule Management" description="Definisikan hard stop, warning, dan deviation beserta approval routing.">
          <form className="stacked-form" onSubmit={handleSubmit}>
            <div className="field-grid two">
              <Field label="Rule ID" value={form.ruleId} onChange={(value) => setField(setForm, "ruleId", value)} />
              <Field label="Rule Name" value={form.ruleName} onChange={(value) => setField(setForm, "ruleName", value)} />
            </div>
            <div className="field-grid four">
              <Field label="Rule Type" value={form.ruleType} onChange={(value) => setField(setForm, "ruleType", value)} as="select" options={ruleTypes} />
              <Field label="Metric Key" value={form.metricKey} onChange={(value) => setField(setForm, "metricKey", value)} as="select" options={metrics} />
              <Field label="Operator" value={form.operator} onChange={(value) => setField(setForm, "operator", value)} as="select" options={operators} />
              <Field label="Threshold" type="number" value={form.thresholdValue} onChange={(value) => setField(setForm, "thresholdValue", value)} />
            </div>
            <div className="field-grid four">
              <Field label="Routing" value={form.actionRouting} onChange={(value) => setField(setForm, "actionRouting", value)} as="select" options={routings} />
              <Field label="Base Approver" value={form.baseApprover} onChange={(value) => setField(setForm, "baseApprover", value)} />
              <Field label="Escalated Approver" value={form.escalatedApprover} onChange={(value) => setField(setForm, "escalatedApprover", value)} />
              <Field label="Committee Approver" value={form.committeeApprover} onChange={(value) => setField(setForm, "committeeApprover", value)} />
            </div>
            <div className="field-grid three">
              <Field label="Board Approver" value={form.boardApprover} onChange={(value) => setField(setForm, "boardApprover", value)} />
              <Field label="Justification Required" value={form.justificationRequired} onChange={(value) => setField(setForm, "justificationRequired", value)} as="select" options={["true", "false"]} />
              <Field label="Active" value={form.active} onChange={(value) => setField(setForm, "active", value)} as="select" options={["true", "false"]} />
            </div>
            <TextAreaField label="Condition / Parameter" value={form.conditionExpression} onChange={(value) => setField(setForm, "conditionExpression", value)} />
            <TextAreaField label="Message Template" value={form.messageTemplate} onChange={(value) => setField(setForm, "messageTemplate", value)} />
            <button type="submit" className="primary-button">Save Rule</button>
          </form>
        </Panel>

        <Panel title="Rule Catalogue" description="Rule aktif yang saat ini dibaca approval engine.">
          <div className="table-like">
            <div className="table-row table-head">
              <span>ID</span>
              <span>Name</span>
              <span>Type</span>
              <span>Condition</span>
              <span>Routing</span>
            </div>
            {rules.map((rule) => (
              <div key={rule.ruleId} className="table-row">
                <span>{rule.ruleId}</span>
                <span>{rule.ruleName}</span>
                <span>{rule.ruleType}</span>
                <span>{rule.conditionExpression}</span>
                <span>{rule.actionRouting}</span>
              </div>
            ))}
          </div>
        </Panel>
      </div>
    </div>
  );
}
