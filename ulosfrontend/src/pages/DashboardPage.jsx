import { useEffect, useState } from "react";
import { api } from "../lib/api";
import { Notice, Panel, StatCard } from "../components/ui";
import { formatCurrency } from "../lib/forms";

export default function DashboardPage() {
  const [summary, setSummary] = useState(null);
  const [message, setMessage] = useState(null);

  useEffect(() => {
    loadSummary();
  }, []);

  async function loadSummary() {
    try {
      setSummary(await api("/api/dashboard/summary"));
    } catch (error) {
      setMessage({ type: "error", text: error.message });
    }
  }

  return (
    <div className="page-stack">
      <Notice type={message?.type} message={message?.text} />
      <div className="stats-grid">
        <StatCard label="Total Customers" value={summary?.totalCustomers ?? 0} tone="amber" />
        <StatCard label="Total Applications" value={summary?.totalApplications ?? 0} tone="navy" />
        <StatCard label="In Review" value={summary?.inReviewApplications ?? 0} tone="green" />
        <StatCard label="Open Tasks" value={summary?.openTasks ?? 0} tone="rose" />
        <StatCard label="Exposure" value={formatCurrency(summary?.totalProposedExposure)} tone="slate" />
      </div>

      <div className="two-column">
        <Panel title="Approval Posture" description="Ringkasan pipeline approval saat ini.">
          <div className="detail-grid">
            <Detail label="Draft" value={summary?.draftApplications ?? 0} />
            <Detail label="Approved" value={summary?.approvedApplications ?? 0} />
            <Detail label="Rejected" value={summary?.rejectedApplications ?? 0} />
            <Detail label="Hard Stop" value={summary?.hardStopApplications ?? 0} />
            <Detail label="Pending TBO" value={summary?.pendingTboCount ?? 0} />
          </div>
        </Panel>

        <Panel title="Workspace Focus" description="Gunakan menu kiri untuk masuk ke modul operasional yang sesuai.">
          <div className="badge-stack">
            <span className="badge">Customer 360 dan financial spreads</span>
            <span className="badge">Application & facility structuring</span>
            <span className="badge">Approval inbox berdasarkan role</span>
            <span className="badge">Rule engine dan BPMN version control</span>
            <span className="badge">User management dan hak akses</span>
          </div>
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
