import { useEffect, useState } from "react";
import { api } from "../lib/api";
import { JsonField, Notice, Panel, Field, TextAreaField, EmptyState, setField } from "../components/ui";
import { parseJson, sampleJson, sanitize } from "../lib/forms";

const companyTypes = ["PT", "CV", "FA", "KOPERASI", "YAYASAN"];

function createForm() {
  return {
    cifNumber: "",
    companyName: "",
    companyType: "PT",
    dateOfEstablishment: "",
    placeOfEstablishment: "",
    taxId: "",
    businessLicense: "",
    officeAddress: "",
    factoryAddress: "",
    keyManagementJson: sampleJson([{ name: "Direktur Utama", nationalIdNumber: "3174xxxx", title: "President Director" }]),
    shareholdersJson: sampleJson([{ name: "PT Holding Nusantara", ownershipPercentage: 75, shareNominal: 7500000000 }]),
    relatedPartiesJson: sampleJson([]),
    financialStatementsJson: sampleJson([]),
    consolidatedFinancialStatementsJson: sampleJson([]),
    bankStatementsJson: sampleJson([]),
    suppliersJson: sampleJson([]),
    buyersJson: sampleJson([]),
    competitorsJson: sampleJson([])
  };
}

export default function CustomersPage() {
  const [form, setForm] = useState(createForm());
  const [customers, setCustomers] = useState([]);
  const [selectedCustomer, setSelectedCustomer] = useState(null);
  const [message, setMessage] = useState(null);

  useEffect(() => {
    refresh();
  }, []);

  async function refresh(selectedCif) {
    try {
      const list = await api("/api/customers");
      setCustomers(list);
      const cif = selectedCif || list[0]?.cifNumber;
      if (cif) {
        setSelectedCustomer(await api(`/api/customers/${cif}`));
      } else {
        setSelectedCustomer(null);
      }
    } catch (error) {
      setMessage({ type: "error", text: error.message });
    }
  }

  async function handleSubmit(event) {
    event.preventDefault();
    try {
      const payload = sanitize({
        ...form,
        keyManagement: parseJson("Key Management", form.keyManagementJson),
        shareholders: parseJson("Shareholders", form.shareholdersJson),
        relatedParties: parseJson("Related Parties", form.relatedPartiesJson),
        financialStatements: parseJson("Financial Statements", form.financialStatementsJson),
        consolidatedFinancialStatements: parseJson("Consolidated Statements", form.consolidatedFinancialStatementsJson),
        bankStatements: parseJson("Bank Statements", form.bankStatementsJson),
        suppliers: parseJson("Suppliers", form.suppliersJson),
        buyers: parseJson("Buyers", form.buyersJson),
        competitors: parseJson("Competitors", form.competitorsJson)
      });
      const saved = await api("/api/customers", { method: "POST", body: JSON.stringify(payload) });
      setForm(createForm());
      setMessage({ type: "success", text: `Customer ${saved.companyName} saved.` });
      await refresh(saved.cifNumber);
    } catch (error) {
      setMessage({ type: "error", text: error.message });
    }
  }

  return (
    <div className="page-stack">
      <Notice type={message?.type} message={message?.text} />
      <div className="two-column">
        <Panel title="Customer / Portfolio Management" description="Master data perusahaan, key management, shareholder, financial statement, bank statement, dan business operation.">
          <form className="stacked-form" onSubmit={handleSubmit}>
            <div className="field-grid three">
              <Field label="CIF Number" value={form.cifNumber} onChange={(value) => setField(setForm, "cifNumber", value)} />
              <Field label="Company Name" value={form.companyName} onChange={(value) => setField(setForm, "companyName", value)} />
              <Field label="Company Type" value={form.companyType} onChange={(value) => setField(setForm, "companyType", value)} as="select" options={companyTypes} />
            </div>
            <div className="field-grid four">
              <Field label="Date of Establishment" type="date" value={form.dateOfEstablishment} onChange={(value) => setField(setForm, "dateOfEstablishment", value)} />
              <Field label="Place of Establishment" value={form.placeOfEstablishment} onChange={(value) => setField(setForm, "placeOfEstablishment", value)} />
              <Field label="NPWP" value={form.taxId} onChange={(value) => setField(setForm, "taxId", value)} />
              <Field label="NIB / SIUP / TDP" value={form.businessLicense} onChange={(value) => setField(setForm, "businessLicense", value)} />
            </div>
            <TextAreaField label="Office Address" value={form.officeAddress} onChange={(value) => setField(setForm, "officeAddress", value)} />
            <TextAreaField label="Factory / Operation Address" value={form.factoryAddress} onChange={(value) => setField(setForm, "factoryAddress", value)} />
            <JsonField label="Key Management JSON" value={form.keyManagementJson} onChange={(value) => setField(setForm, "keyManagementJson", value)} />
            <JsonField label="Shareholder Structure JSON" value={form.shareholdersJson} onChange={(value) => setField(setForm, "shareholdersJson", value)} />
            <JsonField label="Related / Collateral Provider JSON" value={form.relatedPartiesJson} onChange={(value) => setField(setForm, "relatedPartiesJson", value)} />
            <JsonField label="Financial Statement Stand-alone JSON" value={form.financialStatementsJson} onChange={(value) => setField(setForm, "financialStatementsJson", value)} />
            <JsonField label="Financial Statement Consolidated JSON" value={form.consolidatedFinancialStatementsJson} onChange={(value) => setField(setForm, "consolidatedFinancialStatementsJson", value)} />
            <JsonField label="Bank Statement JSON" value={form.bankStatementsJson} onChange={(value) => setField(setForm, "bankStatementsJson", value)} />
            <JsonField label="Suppliers JSON" value={form.suppliersJson} onChange={(value) => setField(setForm, "suppliersJson", value)} />
            <JsonField label="Buyers JSON" value={form.buyersJson} onChange={(value) => setField(setForm, "buyersJson", value)} />
            <JsonField label="Competitors JSON" value={form.competitorsJson} onChange={(value) => setField(setForm, "competitorsJson", value)} />
            <button type="submit" className="primary-button">Save Customer</button>
          </form>
        </Panel>

        <Panel title="Customer Book" description="Daftar company yang sudah tersedia di database MySQL.">
          <div className="card-stack">
            {customers.length === 0 ? (
              <EmptyState title="Belum ada customer" body="Masukkan customer pertama untuk memulai portofolio." />
            ) : (
              customers.map((customer) => (
                <button key={customer.cifNumber} type="button" className={`list-card ${selectedCustomer?.cifNumber === customer.cifNumber ? "active" : ""}`} onClick={() => refresh(customer.cifNumber)}>
                  <div>
                    <p className="eyebrow subtle">{customer.companyType}</p>
                    <h3>{customer.companyName}</h3>
                    <p className="panel-copy">CIF {customer.cifNumber}</p>
                  </div>
                </button>
              ))
            )}
          </div>

          {selectedCustomer ? (
            <div className="detail-sheet">
              <h3>{selectedCustomer.companyName}</h3>
              <div className="detail-grid">
                <Detail label="CIF" value={selectedCustomer.cifNumber} />
                <Detail label="Type" value={selectedCustomer.companyType} />
                <Detail label="NPWP" value={selectedCustomer.taxId || "-"} />
                <Detail label="Financial Statements" value={selectedCustomer.financialStatements?.length || 0} />
                <Detail label="Related Parties" value={selectedCustomer.relatedParties?.length || 0} />
                <Detail label="Bank Statements" value={selectedCustomer.bankStatements?.length || 0} />
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
