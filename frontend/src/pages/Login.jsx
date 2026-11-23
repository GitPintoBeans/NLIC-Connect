import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";

/** Member login; retains the existing session-cookie and CSRF flow. */
export default function Login() {
  const navigate = useNavigate();
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const API_BASE_URL = "http://localhost:8080";

  async function handleLogin(event) {
    event.preventDefault();
    setError("");
    setLoading(true);
    try {
      const csrfResponse = await fetch(`${API_BASE_URL}/api/auth/csrf`, {
        method: "GET",
        credentials: "include",
      });
      if (!csrfResponse.ok) throw new Error("Unable to initialize secure login.");
      const csrf = await csrfResponse.json();
      const response = await fetch(`${API_BASE_URL}/api/auth/login`, {
        method: "POST",
        credentials: "include",
        headers: {
          "Content-Type": "application/json",
          [csrf.headerName]: csrf.token,
        },
        body: JSON.stringify({ email: email.trim(), password }),
      });
      if (!response.ok) {
        if (response.status === 401) throw new Error("Invalid email or password.");
        throw new Error("Unable to sign in. Please try again.");
      }
      setPassword("");
      navigate("/", { replace: true });
    } catch (err) {
      setError(err.message || "An unexpected error occurred.");
    } finally {
      setLoading(false);
    }
  }

  return (
    <>
      <div className="auth-panel-heading">
        <span className="auth-panel-kicker">WELCOME BACK</span>
        <h2 id="auth-login-heading">Login</h2>
        <p>Sign in to your NLIC Connect account.</p>
      </div>
      <form onSubmit={handleLogin} className="auth-form">
        <label htmlFor="auth-login-email">Email Address
          <input
            id="auth-login-email" type="email" value={email}
            onChange={(e) => setEmail(e.target.value)}
            placeholder="Enter your email" autoComplete="username" required
          />
        </label>
        <label htmlFor="auth-login-password">Password
          <input
            id="auth-login-password" type="password" value={password}
            onChange={(e) => setPassword(e.target.value)}
            placeholder="Enter your password" autoComplete="current-password" required
          />
        </label>
        <p className="auth-session-note">Your account uses a secure sign-in session.</p>
        {error && <p role="alert" className="auth-alert auth-alert-error">{error}</p>}
        <button className="auth-submit" type="submit" disabled={loading}>
          {loading ? "Signing In..." : "Sign In"}
        </button>
      </form>
      <p className="auth-panel-foot">Don't have an account? <Link to="/register">Create one here.</Link></p>
    </>
  );
}
