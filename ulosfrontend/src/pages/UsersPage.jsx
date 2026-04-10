import { useEffect, useState } from "react";
import { api } from "../lib/api";
import { EmptyState, Field, Notice, Panel, setField } from "../components/ui";
import { sanitize } from "../lib/forms";

function createForm() {
  return {
    id: null,
    username: "",
    password: "",
    fullName: "",
    email: "",
    active: "true",
    roleCodes: ["RM"]
  };
}

export default function UsersPage() {
  const [form, setForm] = useState(createForm());
  const [users, setUsers] = useState([]);
  const [roles, setRoles] = useState([]);
  const [message, setMessage] = useState(null);

  useEffect(() => {
    void loadData();
  }, []);

  async function loadData(selectedUserId = null) {
    try {
      const [userList, roleList] = await Promise.all([api("/api/users"), api("/api/users/roles")]);
      setUsers(userList);
      setRoles(roleList);

      if (!selectedUserId) {
        return;
      }

      const selected = userList.find((item) => item.id === selectedUserId);
      if (selected) {
        setForm({
          id: selected.id,
          username: selected.username,
          password: "",
          fullName: selected.fullName || "",
          email: selected.email || "",
          active: String(selected.active),
          roleCodes: selected.roles || []
        });
      }
    } catch (error) {
      setMessage({ type: "error", text: error.message });
    }
  }

  function resetForm() {
    setForm(createForm());
  }

  function toggleRole(roleCode) {
    setForm((current) => {
      const exists = current.roleCodes.includes(roleCode);
      return {
        ...current,
        roleCodes: exists
          ? current.roleCodes.filter((value) => value !== roleCode)
          : [...current.roleCodes, roleCode]
      };
    });
  }

  async function handleSubmit(event) {
    event.preventDefault();
    try {
      const payload = sanitize({
        username: form.username,
        password: form.password,
        fullName: form.fullName,
        email: form.email,
        active: form.active === "true",
        roleCodes: form.roleCodes
      });
      const method = form.id ? "PUT" : "POST";
      const path = form.id ? `/api/users/${form.id}` : "/api/users";
      const saved = await api(path, {
        method,
        body: JSON.stringify(payload)
      });
      setMessage({ type: "success", text: `User ${saved.username} saved.` });
      await loadData(saved.id);
    } catch (error) {
      setMessage({ type: "error", text: error.message });
    }
  }

  return (
    <div className="page-stack">
      <Notice type={message?.type} message={message?.text} />
      <div className="two-column">
        <Panel
          title="User Management"
          description="Buat user baru, atur role, aktif/nonaktif user, dan kelola hak akses aplikasi."
          actions={<button type="button" className="secondary-button" onClick={resetForm}>New User</button>}
        >
          <form className="stacked-form" onSubmit={handleSubmit}>
            <div className="field-grid two">
              <Field label="Username" value={form.username} onChange={(value) => setField(setForm, "username", value)} />
              <Field label="Password" type="password" value={form.password} onChange={(value) => setField(setForm, "password", value)} placeholder={form.id ? "Kosongkan jika tidak diubah" : ""} />
            </div>
            <div className="field-grid three">
              <Field label="Full Name" value={form.fullName} onChange={(value) => setField(setForm, "fullName", value)} />
              <Field label="Email" type="email" value={form.email} onChange={(value) => setField(setForm, "email", value)} />
              <Field label="Active" value={form.active} onChange={(value) => setField(setForm, "active", value)} as="select" options={["true", "false"]} />
            </div>

            <div className="field">
              <span>Role Access</span>
              <div className="pill-group">
                {roles.map((role) => {
                  const active = form.roleCodes.includes(role.code);
                  return (
                    <button
                      key={role.code}
                      type="button"
                      className={`pill-button ${active ? "active" : ""}`}
                      onClick={() => toggleRole(role.code)}
                    >
                      <strong>{role.code}</strong>
                      <small>{role.name}</small>
                    </button>
                  );
                })}
              </div>
            </div>

            <button type="submit" className="primary-button">
              {form.id ? "Update User" : "Create User"}
            </button>
          </form>
        </Panel>

        <Panel title="User Directory" description="User yang sudah tersimpan di MySQL beserta role aktifnya.">
          <div className="card-stack">
            {users.length === 0 ? (
              <EmptyState title="Belum ada user" body="Tambahkan user pertama agar role-based access dapat digunakan." />
            ) : (
              users.map((user) => (
                <button key={user.id} type="button" className={`list-card ${form.id === user.id ? "active" : ""}`} onClick={() => loadData(user.id)}>
                  <div>
                    <p className="eyebrow subtle">{user.username}</p>
                    <h3>{user.fullName}</h3>
                    <p className="panel-copy">{user.email || "Email belum diisi"}</p>
                  </div>
                  <div className="list-meta">
                    <span className={`badge ${user.active ? "" : "muted"}`}>{user.active ? "ACTIVE" : "INACTIVE"}</span>
                    <span className="badge accent">{(user.roles || []).join(", ")}</span>
                  </div>
                </button>
              ))
            )}
          </div>
        </Panel>
      </div>
    </div>
  );
}
