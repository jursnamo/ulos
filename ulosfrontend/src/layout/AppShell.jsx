import { Link, useLocation } from "react-router-dom";

const navItems = [
  { to: "/", label: "Dashboard" },
  { to: "/customers", label: "Customer 360" },
  { to: "/applications", label: "Applications" },
  { to: "/approvals", label: "Approvals" },
  { to: "/rules", label: "Rules" },
  { to: "/bpmn", label: "BPMN", roles: ["ADMIN", "WORKFLOW_ADMIN"] },
  { to: "/users", label: "Users", roles: ["ADMIN"] }
];

export default function AppShell({ user, onLogout, children }) {
  const location = useLocation();
  const roles = user?.roles || [];

  return (
    <div className="app-shell">
      <div className="sidebar">
        <div className="brand">
          <p className="eyebrow">ULOS Enterprise</p>
          <h1>Corporate LOS</h1>
        </div>

        <nav className="nav-menu">
          {navItems
            .filter((item) => !item.roles || item.roles.some((role) => roles.includes(role)))
            .map((item) => (
              <Link key={item.to} className={`nav-link ${location.pathname === item.to ? "active" : ""}`} to={item.to}>
                {item.label}
              </Link>
            ))}
        </nav>

        <div className="user-card">
          <strong>{user?.fullName}</strong>
          <span>{user?.username}</span>
          <p>{roles.join(", ")}</p>
          <button type="button" className="ghost-button" onClick={onLogout}>
            Logout
          </button>
        </div>
      </div>

      <div className="content-area">
        <header className="topbar">
          <div>
            <p className="eyebrow subtle">Approval Workflow + MySQL + Spring Boot</p>
            <h2>{location.pathname === "/" ? "Executive Dashboard" : "Operational Workspace"}</h2>
          </div>
        </header>
        {children}
      </div>
    </div>
  );
}
