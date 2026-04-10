import { useState } from "react";
import { login } from "../lib/api";
import { Notice } from "../components/ui";

export default function LoginPage({ onLogin }) {
  const [form, setForm] = useState({ username: "admin", password: "admin123" });
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState(null);

  async function handleSubmit(event) {
    event.preventDefault();
    try {
      setLoading(true);
      setMessage(null);
      const response = await login(form.username, form.password);
      onLogin(response.user);
    } catch (error) {
      setMessage({ type: "error", text: error.message });
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="login-shell">
      <div className="login-card">
        <p className="eyebrow">ULOS Enterprise</p>
        <h1>Login</h1>
        <p className="panel-copy">
          Masuk untuk mengelola customer 360, pengajuan kredit, workflow approval, BPMN versioning, dan user management.
        </p>
        <Notice type={message?.type} message={message?.text} />
        <form className="stacked-form" onSubmit={handleSubmit}>
          <label className="field">
            <span>Username</span>
            <input value={form.username} onChange={(event) => setForm((current) => ({ ...current, username: event.target.value }))} />
          </label>
          <label className="field">
            <span>Password</span>
            <input type="password" value={form.password} onChange={(event) => setForm((current) => ({ ...current, password: event.target.value }))} />
          </label>
          <button type="submit" className="primary-button" disabled={loading}>
            {loading ? "Signing in..." : "Sign In"}
          </button>
        </form>

        <div className="login-hint">
          <strong>Sample account</strong>
          <span><code>admin / admin123</code></span>
        </div>
      </div>
    </div>
  );
}
