import { useEffect, useState } from "react";
import { api } from "../lib/api";
import { Notice, Panel, Field, TextAreaField, EmptyState } from "../components/ui";
import { formatCurrency } from "../lib/forms";

export default function ApprovalsPage({ user }) {
  const [tasks, setTasks] = useState([]);
  const [forms, setForms] = useState({});
  const [message, setMessage] = useState(null);

  useEffect(() => {
    loadTasks();
  }, []);

  async function loadTasks() {
    try {
      setTasks(await api("/api/approvals/tasks"));
    } catch (error) {
      setMessage({ type: "error", text: error.message });
    }
  }

  async function handleComplete(taskId) {
    try {
      await api(`/api/approvals/tasks/${taskId}/complete`, {
        method: "POST",
        body: JSON.stringify(forms[taskId] || { decision: "APPROVE", decisionNotes: "", decidedBy: user?.username || "" })
      });
      setMessage({ type: "success", text: `Task ${taskId} completed.` });
      await loadTasks();
    } catch (error) {
      setMessage({ type: "error", text: error.message });
    }
  }

  return (
    <div className="page-stack">
      <Notice type={message?.type} message={message?.text} />
      <Panel title="Approval Inbox" description="Task aktif berdasarkan role user yang login.">
        <div className="card-stack">
          {tasks.length === 0 ? (
            <EmptyState title="Tidak ada task aktif" body="Semua queue approval sedang kosong untuk role Anda." />
          ) : (
            tasks.map((task) => (
              <article key={task.taskId} className="task-card">
                <div className="action-row">
                  <div>
                    <p className="eyebrow subtle">{task.approvalTier}</p>
                    <h3>{task.companyName}</h3>
                    <p className="panel-copy">{task.taskName} - {task.applicationId}</p>
                  </div>
                  <span className="badge">{formatCurrency(task.proposedExposure)}</span>
                </div>
                <div className="field-grid three">
                  <Field label="Decision" value={forms[task.taskId]?.decision || "APPROVE"} onChange={(value) => setTaskField(setForms, task.taskId, "decision", value)} as="select" options={["APPROVE", "REJECT"]} />
                  <Field label="Decided By" value={forms[task.taskId]?.decidedBy || user?.username || ""} onChange={(value) => setTaskField(setForms, task.taskId, "decidedBy", value)} />
                  <Field label="Assignee" value={task.assignee || "-"} onChange={() => {}} disabled />
                </div>
                <TextAreaField label="Decision Notes" value={forms[task.taskId]?.decisionNotes || ""} onChange={(value) => setTaskField(setForms, task.taskId, "decisionNotes", value)} />
                <button type="button" className="primary-button" onClick={() => handleComplete(task.taskId)}>Complete Task</button>
              </article>
            ))
          )}
        </div>
      </Panel>
    </div>
  );
}

function setTaskField(setter, taskId, key, value) {
  setter((current) => ({
    ...current,
    [taskId]: {
      decision: "APPROVE",
      decisionNotes: "",
      decidedBy: "",
      ...current[taskId],
      [key]: value
    }
  }));
}
