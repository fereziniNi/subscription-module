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
        <h1>Create Subscription</h1>

        {loadingUser ? (
          <p>Loading authenticated user...</p>
        ) : (
          <form onSubmit={handleSubmit}>
            <p><strong>id do usuario:</strong> {form.customerId}</p>

            <select name="planType" value={form.planType} onChange={handleChange}>
              <option value="BASIC">BASIC</option>
              <option value="PLUS">PLUS</option>
              <option value="PRO">PRO</option>
            </select>

            <select name="billingCycle" value={form.billingCycle} onChange={handleChange}>
              <option value="MONTHLY">MONTHLY</option>
              <option value="YEARLY">YEARLY</option>
            </select>

            <div style={{ marginTop: 12, marginBottom: 12 }}>
              <p><strong>Precos das assinaturas:</strong></p>
              <p>BASIC: mensal 29.90 | anual 215.28</p>
              <p>PLUS: mensal 49.90 | anual 359.28</p>
              <p>PRO: mensal 79.90 | anual 575.28</p>
              <p>
                <strong>Preco selecionado:</strong>{" "}
                {PLAN_PRICES[form.planType][form.billingCycle]}
              </p>
            </div>

            <button type="submit">Create</button>
          </form>
        )}

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

        <button style={{ marginTop: 16 }} onClick={() => navigate(-1)}>
          Back
        </button>
      </div>
    </div>
  );
}
