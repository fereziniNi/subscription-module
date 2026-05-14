import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import {
  listSubscriptionsByCustomerId,
  changeSubscriptionPlan,
  cancelSubscriptionImmediately,
  cancelSubscriptionAtPeriodEnd,
  reverseScheduledCancellation,
  renewSubscription,
} from "../api/subscriptions";
import { getAuthenticatedUserId } from "../api/me";
import { advanceSimulatedTime, getSimulatedTime, resetSimulatedTime } from "../api/time";

export default function SubscriptionsPage() {
  const navigate = useNavigate();

  const [customerId, setCustomerId] = useState("");
  const [subscriptions, setSubscriptions] = useState([]);
  const [error, setError] = useState("");
  const [message, setMessage] = useState("");
  const [loadingUser, setLoadingUser] = useState(true);
  const [currentDate, setCurrentDate] = useState("");
  const [loadingTime, setLoadingTime] = useState(true);

  useEffect(() => {
    async function loadAuthenticatedUser() {
      try {
        const userId = await getAuthenticatedUserId();
        setCustomerId(userId);

        try {
          const data = await listSubscriptionsByCustomerId(userId);
          setSubscriptions(data);
        } catch (err) {
          setError(err?.response?.data?.message || "Could not load subscriptions.");
        }
      } catch (err) {
        setError(err?.response?.data?.message || "Could not identify authenticated user.");
      } finally {
        setLoadingUser(false);
      }
    }

    loadAuthenticatedUser();
  }, []);

  useEffect(() => {
    async function loadTime() {
      try {
        const data = await getSimulatedTime();
        setCurrentDate(data.currentDate);
      } catch (err) {
        setError(err?.response?.data?.message || "Could not load simulated time.");
      } finally {
        setLoadingTime(false);
      }
    }

    loadTime();
  }, []);

  async function loadSubscriptions({ clearMessage = true } = {}) {
    if (!customerId) return;

    try {
      const data = await listSubscriptionsByCustomerId(customerId);
      setSubscriptions(data);
      setError("");
      if (clearMessage) {
        setMessage("");
      }
    } catch (err) {
      setError(err?.response?.data?.message || "Could not load subscriptions.");
    }
  }

  async function handleAdvanceTime(months) {
    setError("");

    try {
      const data = await advanceSimulatedTime(months);
      setCurrentDate(data.currentDate);
      setMessage(`Simulated time advanced ${months} month${months > 1 ? "s" : ""}.`);
      await loadSubscriptions({ clearMessage: false });
    } catch (err) {
      setError(err?.response?.data?.message || "Could not advance simulated time.");
    }
  }

  async function handleResetTime() {
    setError("");

    try {
      const data = await resetSimulatedTime();
      setCurrentDate(data.currentDate);
      setMessage("Simulated time reset.");
      await loadSubscriptions({ clearMessage: false });
    } catch (err) {
      setError(err?.response?.data?.message || "Could not reset simulated time.");
    }
  }

  async function handleChangePlan(id, planType) {
    setError("");

    try {
      const updated = await changeSubscriptionPlan(id, { planType });
      setMessage(`Plan updated for subscription ${updated.id}.`);
      await loadSubscriptions({ clearMessage: false });
    } catch (err) {
      setError(err?.response?.data?.message || "Could not change plan.");
    }
  }

  async function handleRenew(id, paymentApproved) {
    setError("");

    try {
      await renewSubscription(id, { paymentApproved });
      setMessage(
        paymentApproved
          ? "Subscription renewed with approved payment."
          : "Subscription suspended after rejected payment."
      );
      await loadSubscriptions({ clearMessage: false });
    } catch (err) {
      setError(err?.response?.data?.message || "Could not renew subscription.");
    }
  }

  async function handleCancel(id) {
    setError("");

    try {
      await cancelSubscriptionImmediately(id);
      setMessage("Subscription cancelled.");
      await loadSubscriptions({ clearMessage: false });
    } catch (err) {
      setError(err?.response?.data?.message || "Could not cancel subscription.");
    }
  }

  async function handleScheduleCancel(id) {
    setError("");

    try {
      await cancelSubscriptionAtPeriodEnd(id);
      setMessage("Cancellation scheduled.");
      await loadSubscriptions({ clearMessage: false });
    } catch (err) {
      setError(err?.response?.data?.message || "Could not schedule cancellation.");
    }
  }

  async function handleReverseCancel(id) {
    setError("");

    try {
      await reverseScheduledCancellation(id);
      setMessage("Scheduled cancellation reversed.");
      await loadSubscriptions({ clearMessage: false });
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

            <div
              style={{
                border: "1px solid #ddd",
                borderRadius: 8,
                padding: 12,
                marginTop: 16,
              }}
            >
              <h2 style={{ fontSize: 18, marginTop: 0 }}>Painel de tempo</h2>
              <p>
                <strong>Current simulated date:</strong>{" "}
                {loadingTime ? "Loading..." : currentDate}
              </p>
              <div style={{ display: "grid", gap: 8 }}>
                <button onClick={() => handleAdvanceTime(1)}>Advance 1 month</button>
                <button onClick={() => handleAdvanceTime(12)}>Advance 12 months</button>
                <button onClick={handleResetTime}>Reset time</button>
              </div>
            </div>

            <button onClick={() => loadSubscriptions()}>Refresh subscriptions</button>

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
                      <button onClick={() => handleRenew(subscription.id, true)}>Renew approved</button>
                      <button onClick={() => handleRenew(subscription.id, false)}>Renew rejected</button>
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

        <button style={{ marginTop: 16 }} onClick={() => navigate(-1)}>
          Back
        </button>
      </div>
    </div>
  );
}
