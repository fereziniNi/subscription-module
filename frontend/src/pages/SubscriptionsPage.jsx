import { useEffect, useState } from "react";
import {
  listSubscriptionsByCustomerId,
  changeSubscriptionPlan,
  cancelSubscriptionImmediately,
  cancelSubscriptionAtPeriodEnd,
  reverseScheduledCancellation,
  renewSubscription,
} from "../api/subscriptions";
import { getAuthenticatedUserId } from "../api/me";

export default function SubscriptionsPage() {
  const [customerId, setCustomerId] = useState("");
  const [subscriptions, setSubscriptions] = useState([]);
  const [error, setError] = useState("");
  const [message, setMessage] = useState("");
  const [loadingUser, setLoadingUser] = useState(true);

  useEffect(() => {
    async function loadAuthenticatedUser() {
      try {
        const userId = await getAuthenticatedUserId();
        setCustomerId(userId);
      } catch (err) {
        setError(err?.response?.data?.message || "Could not identify authenticated user.");
      } finally {
        setLoadingUser(false);
      }
    }

    loadAuthenticatedUser();
  }, []);

  async function loadSubscriptions() {
    if (!customerId) return;

    setError("");
    setMessage("");

    try {
      const data = await listSubscriptionsByCustomerId(customerId);
      setSubscriptions(data);
    } catch (err) {
      setError(err?.response?.data?.message || "Could not load subscriptions.");
    }
  }

  useEffect(() => {
    if (customerId) {
      loadSubscriptions();
    }
  }, [customerId]);

  async function handleChangePlan(id, planType) {
    try {
      const updated = await changeSubscriptionPlan(id, { planType });
      setMessage(`Plan updated for subscription ${updated.id}.`);
      await loadSubscriptions();
    } catch (err) {
      setError(err?.response?.data?.message || "Could not change plan.");
    }
  }

  async function handleRenew(id) {
    try {
      await renewSubscription(id, { paymentApproved: true });
      setMessage("Subscription renewed.");
      await loadSubscriptions();
    } catch (err) {
      setError(err?.response?.data?.message || "Could not renew subscription.");
    }
  }

  async function handleCancel(id) {
    try {
      await cancelSubscriptionImmediately(id);
      setMessage("Subscription cancelled.");
      await loadSubscriptions();
    } catch (err) {
      setError(err?.response?.data?.message || "Could not cancel subscription.");
    }
  }

  async function handleScheduleCancel(id) {
    try {
      await cancelSubscriptionAtPeriodEnd(id);
      setMessage("Cancellation scheduled.");
      await loadSubscriptions();
    } catch (err) {
      setError(err?.response?.data?.message || "Could not schedule cancellation.");
    }
  }

  async function handleReverseCancel(id) {
    try {
      await reverseScheduledCancellation(id);
      setMessage("Scheduled cancellation reversed.");
      await loadSubscriptions();
    } catch (err) {
      setError(err?.response?.data?.message || "Could not reverse cancellation.");
    }
  }

  return (
    <div className="page">
      <div className="card" style={{ maxWidth: 900 }}>
        <h1>My Subscriptions</h1>

        {loadingUser ? (
          <p>Loading authenticated user...</p>
        ) : (
          <>
            <input value={customerId} readOnly />

            <button onClick={loadSubscriptions}>Refresh subscriptions</button>

            {error && <p className="error">{error}</p>}
            {message && <p>{message}</p>}

            <div style={{ marginTop: 20 }}>
              {subscriptions.length === 0 ? (
                <p>No subscriptions found.</p>
              ) : (
                subscriptions.map((subscription) => (
                  <div
                    key={subscription.id}
                    style={{
                      border: "1px solid #ddd",
                      borderRadius: 8,
                      padding: 12,
                      marginBottom: 12,
                    }}
                  >
                    <p><strong>ID:</strong> {subscription.id}</p>
                    <p><strong>Status:</strong> {subscription.status}</p>
                    <p><strong>Plan:</strong> {subscription.planType}</p>
                    <p><strong>Cycle:</strong> {subscription.billingCycle}</p>
                    <p><strong>Amount:</strong> {subscription.amount}</p>
                    <p><strong>Period start:</strong> {subscription.periodStartDate}</p>
                    <p><strong>Period end:</strong> {subscription.periodEndDate}</p>
                    <p><strong>Scheduled plan:</strong> {subscription.scheduledPlanType || "-"}</p>
                    <p><strong>Prorated charge:</strong> {subscription.proratedChargeAmount || "-"}</p>
                    <p><strong>Cancellation scheduled:</strong> {String(subscription.cancellationScheduled)}</p>

                    <div style={{ display: "grid", gap: 8, marginTop: 12 }}>
                      <button onClick={() => handleChangePlan(subscription.id, "BASIC")}>Change to BASIC</button>
                      <button onClick={() => handleChangePlan(subscription.id, "PLUS")}>Change to PLUS</button>
                      <button onClick={() => handleChangePlan(subscription.id, "PRO")}>Change to PRO</button>
                      <button onClick={() => handleRenew(subscription.id)}>Renew</button>
                      <button onClick={() => handleCancel(subscription.id)}>Cancel immediately</button>
                      <button onClick={() => handleScheduleCancel(subscription.id)}>Cancel at period end</button>
                      <button onClick={() => handleReverseCancel(subscription.id)}>Reverse cancellation</button>
                    </div>
                  </div>
                ))
              )}
            </div>
          </>
        )}
      </div>
    </div>
  );
}