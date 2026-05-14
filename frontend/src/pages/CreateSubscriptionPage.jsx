import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { createSubscription } from "../api/subscriptions";
import { getAuthenticatedUserId } from "../api/me";

const PLAN_PRICES = {
  BASIC: {
    MONTHLY: "29.90",
    YEARLY: "215.28",
  },
  PLUS: {
    MONTHLY: "49.90",
    YEARLY: "359.28",
  },
  PRO: {
    MONTHLY: "79.90",
    YEARLY: "575.28",
  },
};

export default function CreateSubscriptionPage() {
  const navigate = useNavigate();

  const [form, setForm] = useState({
    customerId: "",
    planType: "BASIC",
    billingCycle: "MONTHLY",
  });

  const [error, setError] = useState("");
  const [created, setCreated] = useState(null);
  const [loadingUser, setLoadingUser] = useState(true);
  const selectedPrice = PLAN_PRICES[form.planType][form.billingCycle];

  useEffect(() => {
    async function loadAuthenticatedUser() {
      try {
        const userId = await getAuthenticatedUserId();
        setForm((prev) => ({
          ...prev,
          customerId: userId,
        }));
      } catch (err) {
        setError(err?.response?.data?.message || "Could not identify authenticated user.");
      } finally {
        setLoadingUser(false);
      }
    }

    loadAuthenticatedUser();
  }, []);

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
        <div>
          <h1 className="page-title">Create Subscription</h1>
          <p className="page-subtitle">Choose a plan and billing cycle before creating a subscription.</p>
        </div>

        {loadingUser ? (
          <p className="muted">Loading authenticated user...</p>
        ) : (
          <form className="form-stack" onSubmit={handleSubmit}>
            <div>
              <p className="price-name">User</p>
              <span className="id-chip">{form.customerId}</span>
            </div>

            <label className="field-label">
              Plan
              <select name="planType" value={form.planType} onChange={handleChange}>
                <option value="BASIC">BASIC</option>
                <option value="PLUS">PLUS</option>
                <option value="PRO">PRO</option>
              </select>
            </label>

            <label className="field-label">
              Billing cycle
              <select name="billingCycle" value={form.billingCycle} onChange={handleChange}>
                <option value="MONTHLY">MONTHLY</option>
                <option value="YEARLY">YEARLY</option>
              </select>
            </label>

            <div className="panel">
              <div className="panel-header">
                <h2 className="panel-title">Precos das assinaturas</h2>
              </div>

              <div className="price-grid">
                {Object.entries(PLAN_PRICES).map(([plan, prices]) => (
                  <div
                    className={`price-option ${form.planType === plan ? "active" : ""}`}
                    key={plan}
                  >
                    <p className="price-name">{plan}</p>
                    <p className="price-value">Mensal {prices.MONTHLY}</p>
                    <p className="muted">Anual {prices.YEARLY}</p>
                  </div>
                ))}
              </div>

              <div className="selected-price">
                <span>Preco selecionado</span>
                <span>{selectedPrice}</span>
              </div>
            </div>

            <button type="submit">Create</button>
          </form>
        )}

        {error && <p className="error">{error}</p>}

        {created && (
          <div className="panel">
            <div className="panel-header">
              <h2 className="panel-title">Created subscription</h2>
              <span className={`badge badge-${created.status.toLowerCase()}`}>{created.status}</span>
            </div>
            <div className="detail-grid">
              <div className="detail-item">
                <p className="detail-label">ID</p>
                <p className="detail-value">{created.id}</p>
              </div>
              <div className="detail-item">
                <p className="detail-label">Plan</p>
                <p className="detail-value">{created.planType}</p>
              </div>
              <div className="detail-item">
                <p className="detail-label">Cycle</p>
                <p className="detail-value">{created.billingCycle}</p>
              </div>
              <div className="detail-item">
                <p className="detail-label">Amount</p>
                <p className="detail-value">{created.amount}</p>
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
