
import { useState } from "react";
import { useNavigate } from "react-router-dom";

/**
 * Member login page for NLIC Connect.
 *
 * Authenticates members through the Spring Boot
 * backend using CSRF-protected HTTP sessions.
 */
function Login() {
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
      // Obtain a CSRF token and associated session cookie.
      const csrfResponse = await fetch(
        `${API_BASE_URL}/api/auth/csrf`,
        {
          method: "GET",
          credentials: "include",
        }
      );

      if (!csrfResponse.ok) {
        throw new Error("Unable to initialize secure login.");
      }

      const csrf = await csrfResponse.json();

      // Submit login credentials with the CSRF token.
      const loginResponse = await fetch(
        `${API_BASE_URL}/api/auth/login`,
        {
          method: "POST",
          credentials: "include",
          headers: {
            "Content-Type": "application/json",
            [csrf.headerName]: csrf.token,
          },
          body: JSON.stringify({
            email: email.trim(),
            password,
          }),
        }
      );

      if (!loginResponse.ok) {
        if (loginResponse.status === 401) {
          throw new Error("Invalid email or password.");
        }

        throw new Error(
          "Unable to sign in. Please try again."
        );
      }

      // The authenticated session is stored by the
      // browser through its HTTP-only session cookie.
      // Do not store passwords in localStorage.
      setPassword("");

      navigate("/", { replace: true });
    } catch (err) {
      setError(
        err.message || "An unexpected error occurred."
      );
    } finally {
      setLoading(false);
    }
  }

  return (
    <main
      style={{
        maxWidth: "420px",
        margin: "80px auto",
        padding: "32px",
      }}
    >
      <h1>NLIC Connect</h1>

      <h2>Member Login</h2>

      <p>
        Welcome to New Life in Christ Ministries.
        Sign in to connect with your church family.
      </p>

      <form onSubmit={handleLogin}>
        <div style={{ marginBottom: "16px" }}>
          <label htmlFor="email">Email Address</label>

          <input
            id="email"
            type="email"
            value={email}
            onChange={(event) =>
              setEmail(event.target.value)
            }
            required
            autoComplete="username"
            style={{
              display: "block",
              width: "100%",
              padding: "10px",
              marginTop: "6px",
              boxSizing: "border-box",
            }}
          />
        </div>

        <div style={{ marginBottom: "20px" }}>
          <label htmlFor="password">Password</label>

          <input
            id="password"
            type="password"
            value={password}
            onChange={(event) =>
              setPassword(event.target.value)
            }
            required
            autoComplete="current-password"
            style={{
              display: "block",
              width: "100%",
              padding: "10px",
              marginTop: "6px",
              boxSizing: "border-box",
            }}
          />
        </div>

        {error && (
          <p role="alert" style={{ color: "crimson" }}>
            {error}
          </p>
        )}

        <button type="submit" disabled={loading}>
          {loading ? "Signing In..." : "Sign In"}
        </button>
      </form>
    </main>
  );
}

export default Login;