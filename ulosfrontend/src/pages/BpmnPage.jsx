import { useEffect, useState } from "react";
import { api } from "../lib/api";
import { Field, JsonField, Notice, Panel, TextAreaField, setField } from "../components/ui";

function createForm() {
  return {
    processKey: "",
    processName: "",
    resourceName: "",
    deployedBy: "",
    changeSummary: "",
    bpmnXml: ""
  };
}

export default function BpmnPage({ user }) {
  const [form, setForm] = useState(createForm());
  const [versions, setVersions] = useState([]);
  const [selectedXml, setSelectedXml] = useState(null);
  const [message, setMessage] = useState(null);

  useEffect(() => {
    loadVersions();
    setForm((current) => ({ ...current, deployedBy: user?.username || "" }));
  }, [user]);

  async function loadVersions() {
    try {
      setVersions(await api("/api/bpmn"));
    } catch (error) {
      setMessage({ type: "error", text: error.message });
    }
  }

  async function loadXml(processKey) {
    try {
      setSelectedXml(await api(`/api/bpmn/${processKey}`));
    } catch (error) {
      setMessage({ type: "error", text: error.message });
    }
  }

  async function handleSubmit(event) {
    event.preventDefault();
    try {
      await api("/api/bpmn/deploy", {
        method: "POST",
        body: JSON.stringify(form)
      });
      setMessage({ type: "success", text: "BPMN deployed and versioned." });
      setForm((current) => ({ ...createForm(), deployedBy: user?.username || "" }));
      await loadVersions();
    } catch (error) {
      setMessage({ type: "error", text: error.message });
    }
  }

  return (
    <div className="page-stack">
      <Notice type={message?.type} message={message?.text} />
      <div className="two-column">
        <Panel title="BPMN Version Registry" description="Registry BPMN yang tersimpan di database dan deployment engine Flowable.">
          <div className="card-stack">
            {versions.map((version) => (
              <button key={`${version.processKey}-${version.version}`} type="button" className="list-card" onClick={() => loadXml(version.processKey)}>
                <div>
                  <p className="eyebrow subtle">{version.processKey}</p>
                  <h3>{version.processName}</h3>
                  <p className="panel-copy">Version {version.version} - {version.active ? "Active" : "Inactive"}</p>
                </div>
                <span className="badge">{version.deployedBy || "system"}</span>
              </button>
            ))}
          </div>
        </Panel>

        <Panel title="Deploy New BPMN" description="Upload BPMN XML baru sekaligus simpan version metadata ke MySQL.">
          <form className="stacked-form" onSubmit={handleSubmit}>
            <div className="field-grid two">
              <Field label="Process Key" value={form.processKey} onChange={(value) => setField(setForm, "processKey", value)} />
              <Field label="Process Name" value={form.processName} onChange={(value) => setField(setForm, "processName", value)} />
            </div>
            <div className="field-grid two">
              <Field label="Resource Name" value={form.resourceName} onChange={(value) => setField(setForm, "resourceName", value)} />
              <Field label="Deployed By" value={form.deployedBy} onChange={(value) => setField(setForm, "deployedBy", value)} />
            </div>
            <TextAreaField label="Change Summary" value={form.changeSummary} onChange={(value) => setField(setForm, "changeSummary", value)} />
            <JsonField label="BPMN XML" value={form.bpmnXml} onChange={(value) => setField(setForm, "bpmnXml", value)} />
            <button type="submit" className="primary-button">Deploy BPMN</button>
          </form>
        </Panel>
      </div>

      {selectedXml ? (
        <Panel title={`XML Preview - ${selectedXml.processDefinitionKey}`} description={`Version ${selectedXml.version} - ${selectedXml.resourceName}`}>
          <pre className="xml-preview">{selectedXml.bpmnXml}</pre>
        </Panel>
      ) : null}
    </div>
  );
}
