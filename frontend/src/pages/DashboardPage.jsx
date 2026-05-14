import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

export default function DashboardPage() {
  const navigate = useNavigate();
  const { logout } = useAuth();

  return (
    <div className="page">
      <div className="card">
        <div>
          <h1 className="page-title">Subscription Module</h1>
          <p className="page-subtitle">Create, renew, cancel and inspect subscription cycles.</p>
        </div>

        <div className="menu">
          <Link to="/subscriptions/create">Create subscription</Link>
          <Link to="/subscriptions">View subscriptions</Link>
          <Link to="/invoices">Generate invoice</Link>
        </div>

        <div className="actions">
          <button className="secondary" onClick={() => navigate(-1)}>Back</button>
          <button className="danger" onClick={logout}>Logout</button>
        </div>
      </div>
    </div>
  );
}
