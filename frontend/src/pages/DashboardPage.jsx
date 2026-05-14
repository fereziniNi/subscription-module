import { Link, useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

export default function DashboardPage() {
  const navigate = useNavigate();
  const { logout } = useAuth();

  return (
    <div className="page">
      <div className="card">
        <h1>Subscription Module</h1>

        <div className="menu">
          <Link to="/subscriptions/create">Create subscription</Link>
          <Link to="/subscriptions">View subscriptions</Link>
          <Link to="/invoices">Generate invoice</Link>
        </div>

        <button onClick={logout}>Logout</button>
        <button onClick={() => navigate(-1)}>Back</button>
      </div>
    </div>
  );
}
