import { useState } from "react";
import { useNavigate, Link } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

export default function LoginPage() {
  const navigate = useNavigate();
  const { login } = useAuth();

  const [form, setForm] = useState({
    username: "",
    password: "",
  });
  const [error, setError] = useState("");

  function handleChange(e) {
    setForm({ ...form, [e.target.name]: e.target.value });
  }

  async function handleSubmit(e) {
    e.preventDefault();
    setError("");

    try {
      await login(form);
      navigate("/");
    } catch {
      setError("Invalid credentials.");
    }
  }

  return (
    <div className="page">
      <form className="card form-stack" onSubmit={handleSubmit}>
        <div>
          <h1 className="page-title">Login</h1>
          <p className="page-subtitle">Access your subscription workspace.</p>
        </div>

        <label className="field-label">
          Email
          <input
            name="username"
            placeholder="you@example.com"
            value={form.username}
            onChange={handleChange}
          />
        </label>

        <label className="field-label">
          Password
          <input
            name="password"
            type="password"
            placeholder="Your password"
            value={form.password}
            onChange={handleChange}
          />
        </label>

        {error && <p className="error">{error}</p>}
        <button type="submit">Sign in</button>
        <p className="auth-footer">
          No account? <Link to="/register">Register</Link>
        </p>
      </form>
    </div>
  );
}
