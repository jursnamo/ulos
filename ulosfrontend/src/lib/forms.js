export function sampleJson(value) {
  return JSON.stringify(value, null, 2);
}

export function parseJson(label, value) {
  try {
    return JSON.parse(value || "[]");
  } catch {
    throw new Error(`${label} JSON tidak valid`);
  }
}

export function sanitize(value) {
  if (Array.isArray(value)) {
    return value.map((item) => sanitize(item));
  }
  if (value && typeof value === "object") {
    return Object.fromEntries(Object.entries(value).map(([key, nested]) => [key, sanitize(nested)]));
  }
  return value === "" ? null : value;
}

export const currencyFormatter = new Intl.NumberFormat("id-ID", {
  style: "currency",
  currency: "IDR",
  maximumFractionDigits: 0
});

export function formatCurrency(value) {
  if (value === null || value === undefined || value === "") return "-";
  return currencyFormatter.format(Number(value));
}

export function formatMetric(value) {
  if (value === null || value === undefined || value === "") return "-";
  return Number(value).toFixed(2);
}
