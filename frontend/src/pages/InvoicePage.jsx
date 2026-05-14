import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { generateInvoice } from "../api/subscriptions";

export default function InvoicePage() {
  const navigate = useNavigate();

  const [subscriptionId, setSubscriptionId] = useState("");
  const [invoice, setInvoice] = useState(null);
  const [error, setError] = useState("");

  async function handleGenerate() {
    setError("");
    setInvoice(null);

    try {
      const data = await generateInvoice(subscriptionId);
      setInvoice(data);
    } catch (err) {
      setError(err?.response?.data?.message || "Could not generate invoice.");
    }
  }

  return (
    <div className="page">
      <div className="card section-stack">
        <div>
          <h1 className="page-title">Generate Invoice</h1>
          <p className="page-subtitle">Create an invoice for a subscription period.</p>
        </div>

        <label className="field-label">
          Subscription ID
          <input
            placeholder="Subscription ID"
            value={subscriptionId}
            onChange={(e) => setSubscriptionId(e.target.value)}
          />
        </label>

        <button onClick={handleGenerate}>Generate</button>

        {error && <p className="error">{error}</p>}

        {invoice && (
          <div className="panel">
            <div className="panel-header">
              <h2 className="panel-title">Invoice</h2>
              <span className="badge badge-neutral">{invoice.amount}</span>
            </div>
            <div className="detail-grid">
              <div className="detail-item">
                <p className="detail-label">Invoice ID</p>
                <p className="detail-value">{invoice.id}</p>
              </div>
              <div className="detail-item">
                <p className="detail-label">Subscription ID</p>
                <p className="detail-value">{invoice.subscriptionId}</p>
              </div>
              <div className="detail-item">
                <p className="detail-label">Start</p>
                <p className="detail-value">{invoice.periodStartDate}</p>
              </div>
              <div className="detail-item">
                <p className="detail-label">End</p>
                <p className="detail-value">{invoice.periodEndDate}</p>
              </div>
            </div>
          </div>
        )}

        <button className="secondary" onClick={() => navigate(-1)}>
          Back
        </button>
      </div>
    </div>
  );
}
