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
      <form className="card" onSubmit={handleSubmit}>
        <h1>Register</h1>
        <input name="name" placeholder="Name" value={form.name} onChange={handleChange} />
        <input name="lastname" placeholder="Lastname" value={form.lastname} onChange={handleChange} />
        <input name="email" placeholder="Email" value={form.email} onChange={handleChange} />
        <input name="password" type="password" placeholder="Password" value={form.password} onChange={handleChange} />
        {error && <p className="error">{error}</p>}
        <button type="submit">Create account</button>
        <p>
          Already registered? <Link to="/login">Login</Link>
        </p>
        <button type="button" onClick={() => navigate(-1)}>
          Back
        </button>
      </form>
    </div>
  );
}
