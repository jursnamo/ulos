export function Notice({ type = "info", message }) {
  if (!message) return null;
  return <div className={`notice ${type}`}>{message}</div>;
}

export function Panel({ title, description, children, actions = null }) {
  return (
    <section className="panel">
      <div className="panel-head">
        <div>
          <h2>{title}</h2>
          {description ? <p className="panel-copy">{description}</p> : null}
        </div>
        {actions}
      </div>
      {children}
    </section>
  );
}

export function Field({ label, value, onChange, type = "text", as = "input", options = [], disabled = false, placeholder = "" }) {
  const resolved = options.map((option) => (typeof option === "string" ? { label: option, value: option } : option));
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

export function TextAreaField({ label, value, onChange, rows = 3 }) {
  return (
    <label className="field">
      <span>{label}</span>
      <textarea rows={rows} value={value ?? ""} onChange={(event) => onChange(event.target.value)} />
    </label>
  );
}

export function JsonField({ label, value, onChange }) {
  return (
    <label className="field json-field">
      <span>{label}</span>
      <textarea rows={10} value={value ?? ""} onChange={(event) => onChange(event.target.value)} spellCheck={false} />
    </label>
  );
}

export function StatCard({ label, value, tone = "default" }) {
  return (
    <article className={`stat-card ${tone}`}>
      <span>{label}</span>
      <strong>{value}</strong>
    </article>
  );
}

export function EmptyState({ title, body }) {
  return (
    <div className="empty-state">
      <h3>{title}</h3>
      <p>{body}</p>
    </div>
  );
}

export function setField(setter, key, value) {
  setter((current) => ({ ...current, [key]: value }));
}
