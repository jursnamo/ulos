import { Navigate, Outlet, Route, Routes, useNavigate } from "react-router-dom";
import { useEffect, useState } from "react";
import { clearToken, getToken, me } from "./lib/api";
import AppShell from "./layout/AppShell";
import DashboardPage from "./pages/DashboardPage";
import CustomersPage from "./pages/CustomersPage";
import ApplicationsPage from "./pages/ApplicationsPage";
import ApprovalsPage from "./pages/ApprovalsPage";
import RulesPage from "./pages/RulesPage";
import BpmnPage from "./pages/BpmnPage";
import UsersPage from "./pages/UsersPage";
import LoginPage from "./pages/LoginPage";

export default function App() {
  const navigate = useNavigate();
  const [loading, setLoading] = useState(true);
  const [user, setUser] = useState(null);

  useEffect(() => {
    void bootstrap();
  }, []);

  async function bootstrap() {
    if (!getToken()) {
      setLoading(false);
      return;
    }

    try {
      const currentUser = await me();
      setUser(currentUser);
    } catch {
      clearToken();
      setUser(null);
    } finally {
      setLoading(false);
    }
  }

  function handleLogout() {
    clearToken();
    setUser(null);
    navigate("/login");
  }

  if (loading) {
    return <div className="screen-center">Loading ULOS Enterprise...</div>;
  }

  return (
    <AppRoutes
      appState={{
        user,
        setUser,
        refreshUser: bootstrap,
        logout: handleLogout
      }}
    />
  );
}

function AppRoutes({ appState }) {
  if (!appState.user) {
    return (
      <Routes>
        <Route path="/login" element={<LoginPage onLogin={appState.setUser} />} />
        <Route path="*" element={<Navigate to="/login" replace />} />
      </Routes>
    );
  }

  return (
    <Routes>
      <Route path="/login" element={<Navigate to="/" replace />} />
      <Route element={<ProtectedLayout appState={appState} />}>
        <Route path="/" element={<DashboardPage user={appState.user} />} />
        <Route path="/customers" element={<CustomersPage user={appState.user} />} />
        <Route path="/applications" element={<ApplicationsPage user={appState.user} />} />
        <Route path="/approvals" element={<ApprovalsPage user={appState.user} />} />
        <Route path="/rules" element={<RulesPage user={appState.user} />} />
        <Route
          path="/bpmn"
          element={(
            <RoleGuard user={appState.user} roles={["ADMIN", "WORKFLOW_ADMIN"]}>
              <BpmnPage user={appState.user} />
            </RoleGuard>
          )}
        />
        <Route
          path="/users"
          element={(
            <RoleGuard user={appState.user} roles={["ADMIN"]}>
              <UsersPage user={appState.user} />
            </RoleGuard>
          )}
        />
        <Route path="*" element={<Navigate to="/" replace />} />
      </Route>
    </Routes>
  );
}

function ProtectedLayout({ appState }) {
  return (
    <AppShell user={appState.user} onLogout={appState.logout}>
      <Outlet />
    </AppShell>
  );
}

function RoleGuard({ user, roles, children }) {
  const allowed = roles.some((role) => user?.roles?.includes(role));
  if (allowed) {
    return children;
  }

  return (
    <div className="page-stack">
      <section className="panel">
        <div className="empty-state">
          <h3>Akses tidak tersedia</h3>
          <p>Role Anda belum memiliki hak akses ke modul ini.</p>
        </div>
      </section>
    </div>
  );
}
