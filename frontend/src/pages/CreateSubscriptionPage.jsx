import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { createSubscription } from "../api/subscriptions";

export default function CreateSubscriptionPage() {
  const navigate = useNavigate();

  const [form, setForm] = useState({
    customerId: "",
    planType: "BASIC",
    billingCycle: "MONTHLY",
  });

  const [error, setError] = useState("");
  const [created, setCreated] = useState(null);

  function handleChange(e) {
    setForm({ ...form, [e.target.name]: e.target.value });
  }

  async function handleSubmit(e) {
    e.preventDefault();
    setError("");
    setCreated(null);

    try {
      const data = await createSubscription(form);
      setCreated(data);
    } catch (err) {
      setError(err?.response?.data?.message || "Could not create subscription.");
    }
  }

  return (
    <div className="page">
      <div className="card">
        <h1>Create Subscription</h1>

        <form onSubmit={handleSubmit}>
          <input
            name="customerId"
            placeholder="Customer ID"
            value={form.customerId}
            onChange={handleChange}
          />

          <select name="planType" value={form.planType} onChange={handleChange}>
            <option value="BASIC">BASIC</option>
            <option value="PLUS">PLUS</option>
            <option value="PRO">PRO</option>
          </select>

          <select name="billingCycle" value={form.billingCycle} onChange={handleChange}>
            <option value="MONTHLY">MONTHLY</option>
            <option value="YEARLY">YEARLY</option>
          </select>

          <button type="submit">Create</button>
        </form>

        {error && <p className="error">{error}</p>}

        {created && (
          <div style={{ marginTop: 16 }}>
            <p><strong>ID:</strong> {created.id}</p>
            <p><strong>Status:</strong> {created.status}</p>
            <p><strong>Plan:</strong> {created.planType}</p>
            <p><strong>Cycle:</strong> {created.billingCycle}</p>
            <p><strong>Amount:</strong> {created.amount}</p>
          </div>
        )}

        <button style={{ marginTop: 16 }} onClick={() => navigate("/")}>
          Back
        </button>
      </div>
    </div>
  );
}