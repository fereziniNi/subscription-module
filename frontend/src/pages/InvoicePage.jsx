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
      <div className="card">
        <h1>Generate Invoice</h1>

        <input
          placeholder="Subscription ID"
          value={subscriptionId}
          onChange={(e) => setSubscriptionId(e.target.value)}
        />

        <button onClick={handleGenerate}>Generate</button>

        {error && <p className="error">{error}</p>}

        {invoice && (
          <div style={{ marginTop: 16 }}>
            <p><strong>Invoice ID:</strong> {invoice.id}</p>
            <p><strong>Subscription ID:</strong> {invoice.subscriptionId}</p>
            <p><strong>Amount:</strong> {invoice.amount}</p>
            <p><strong>Start:</strong> {invoice.periodStartDate}</p>
            <p><strong>End:</strong> {invoice.periodEndDate}</p>
          </div>
        )}

        <button style={{ marginTop: 16 }} onClick={() => navigate(-1)}>
          Back
        </button>
      </div>
    </div>
  );
}
