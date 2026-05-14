import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { registerUser } from "../api/auth";

export default function RegisterPage() {
  const navigate = useNavigate();

  const [form, setForm] = useState({
    name: "",
    lastname: "",
    email: "",
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
      await registerUser(form);
      navigate("/login");
    } catch {
      setError("Could not register user.");
    }
  }

  return (
    <div className="page">
      <form className="card form-stack" onSubmit={handleSubmit}>
        <div>
          <h1 className="page-title">Register</h1>
          <p className="page-subtitle">Create an account to manage subscriptions.</p>
        </div>

        <label className="field-label">
          Name
          <input name="name" placeholder="Name" value={form.name} onChange={handleChange} />
        </label>

        <label className="field-label">
          Lastname
          <input name="lastname" placeholder="Lastname" value={form.lastname} onChange={handleChange} />
        </label>

        <label className="field-label">
          Email
          <input name="email" placeholder="you@example.com" value={form.email} onChange={handleChange} />
        </label>

        <label className="field-label">
          Password
          <input name="password" type="password" placeholder="Your password" value={form.password} onChange={handleChange} />
        </label>

        {error && <p className="error">{error}</p>}
        <button type="submit">Create account</button>
        <p className="auth-footer">
          Already registered? <Link to="/login">Login</Link>
        </p>
        <button className="secondary" type="button" onClick={() => navigate(-1)}>
          Back
        </button>
      </form>
    </div>
  );
}
