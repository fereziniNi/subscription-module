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
import {
  advanceSimulatedTime,
  advanceSimulatedTimeByDays,
  getSimulatedTime,
  resetSimulatedTime,
} from "../api/time";

function statusBadgeClass(status) {
  const normalizedStatus = status?.toLowerCase();

  if (["active", "suspended", "cancelled"].includes(normalizedStatus)) {
    return `badge badge-${normalizedStatus}`;
  }

  return "badge badge-neutral";
}

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

  async function handleAdvanceDays(days) {
    setError("");

    try {
      const data = await advanceSimulatedTimeByDays(days);
      setCurrentDate(data.currentDate);
      setMessage(`Simulated time advanced ${days} day${days > 1 ? "s" : ""}.`);
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
    <div className="page page-wide">
      <div className="card card-wide">
        <div className="page-header">
          <div>
            <h1 className="page-title">My Subscriptions</h1>
            <p className="page-subtitle">Manage cycles, payments, renewals and simulated time.</p>
          </div>
          <button className="secondary" onClick={() => navigate(-1)}>
            Back
          </button>
        </div>

        {loadingUser ? (
          <p className="muted">Loading authenticated user...</p>
        ) : (
          <>
            <div className="panel">
              <p className="price-name">Authenticated user</p>
              <span className="id-chip">{customerId}</span>
            </div>

            <div className="panel">
              <div className="panel-header">
                <h2 className="panel-title">Painel de tempo</h2>
                <span className="time-value">{loadingTime ? "Loading..." : currentDate}</span>
              </div>
              <p>
                <span className="muted">Advance the simulated date to expire cycles and test renewal flows.</span>
              </p>
              <div className="actions">
                <button onClick={() => handleAdvanceDays(1)}>Advance 1 day</button>
                <button onClick={() => handleAdvanceTime(1)}>Advance 1 month</button>
                <button onClick={() => handleAdvanceTime(12)}>Advance 12 months</button>
                <button className="secondary" onClick={handleResetTime}>Reset time</button>
              </div>
            </div>

            <div className="actions">
              <button className="secondary" onClick={() => loadSubscriptions()}>Refresh subscriptions</button>
            </div>

            {error && <p className="error">{error}</p>}
            {message && <p className="notice">{message}</p>}

            <div className="subscription-list">
              {subscriptions.length === 0 ? (
                <div className="empty-state">No subscriptions found.</div>
              ) : (
                subscriptions.map((subscription) => (
                  <div className="subscription-card" key={subscription.id}>
                    <div className="subscription-card-header">
                      <div>
                        <h2 className="panel-title">{subscription.planType} subscription</h2>
                        <p className="subscription-id">{subscription.id}</p>
                      </div>
                      <span className={statusBadgeClass(subscription.status)}>{subscription.status}</span>
                    </div>

                    <div className="detail-grid">
                      <div className="detail-item">
                        <p className="detail-label">Cycle</p>
                        <p className="detail-value">{subscription.billingCycle}</p>
                      </div>
                      <div className="detail-item">
                        <p className="detail-label">Amount</p>
                        <p className="detail-value">{subscription.amount}</p>
                      </div>
                      <div className="detail-item">
                        <p className="detail-label">Period start</p>
                        <p className="detail-value">{subscription.periodStartDate}</p>
                      </div>
                      <div className="detail-item">
                        <p className="detail-label">Period end</p>
                        <p className="detail-value">{subscription.periodEndDate}</p>
                      </div>
                      <div className="detail-item">
                        <p className="detail-label">Scheduled plan</p>
                        <p className="detail-value">{subscription.scheduledPlanType || "-"}</p>
                      </div>
                      <div className="detail-item">
                        <p className="detail-label">Prorated charge</p>
                        <p className="detail-value">{subscription.proratedChargeAmount || "-"}</p>
                      </div>
                      <div className="detail-item">
                        <p className="detail-label">Cancellation scheduled</p>
                        <p className="detail-value">{String(subscription.cancellationScheduled)}</p>
                      </div>
                    </div>

                    <div className="actions compact">
                      <button className="secondary" onClick={() => handleChangePlan(subscription.id, "BASIC")}>Change to BASIC</button>
                      <button className="secondary" onClick={() => handleChangePlan(subscription.id, "PLUS")}>Change to PLUS</button>
                      <button className="secondary" onClick={() => handleChangePlan(subscription.id, "PRO")}>Change to PRO</button>
                      <button onClick={() => handleRenew(subscription.id, true)}>Renew approved</button>
                      <button className="warning" onClick={() => handleRenew(subscription.id, false)}>Renew rejected</button>
                      <button className="danger" onClick={() => handleCancel(subscription.id)}>Cancel immediately</button>
                      <button className="warning" onClick={() => handleScheduleCancel(subscription.id)}>Cancel at period end</button>
                      <button className="secondary" onClick={() => handleReverseCancel(subscription.id)}>Reverse cancellation</button>
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
