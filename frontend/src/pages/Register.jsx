import { useState } from "react";
import { Link } from "react-router-dom";

const emptyForm = {
  firstName: "", lastName: "", email: "", password: "", confirmPassword: "",
};

/** Account registration; retains existing backend endpoint and validation. */
export default function Register() {
  const [formData, setFormData] = useState(emptyForm);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState(false);
  const API_BASE_URL = "http://localhost:8080";

  function handleChange(event) {
    const { name, value } = event.target;
    setFormData((previous) => ({ ...previous, [name]: value }));
  }

  async function handleSubmit(event) {
    event.preventDefault();
    setError("");
    setSuccess(false);
    if (formData.password !== formData.confirmPassword) {
      setError("Passwords do not match.");
      return;
    }
    if (formData.password.length < 12 || formData.password.length > 64) {
      setError("Password must contain between 12 and 64 characters.");
      return;
    }

    setLoading(true);
    try {
      const csrfResponse = await fetch(`${API_BASE_URL}/api/auth/csrf`, {
        method: "GET",
        credentials: "include",
      });
      if (!csrfResponse.ok) throw new Error("Unable to initialize secure registration.");
      const csrf = await csrfResponse.json();
      const response = await fetch(`${API_BASE_URL}/api/auth/register`, {
        method: "POST",
        credentials: "include",
        headers: {
          "Content-Type": "application/json",
          [csrf.headerName]: csrf.token,
        },
        body: JSON.stringify({
          firstName: formData.firstName.trim(),
          lastName: formData.lastName.trim(),
          email: formData.email.trim().toLowerCase(),
          password: formData.password,
        }),
      });
      if (!response.ok) {
        if (response.status === 409) throw new Error("An account with this email address already exists.");
        if (response.status === 400) throw new Error("Please check your information and try again.");
        throw new Error("Registration failed. Please try again.");
      }
      setSuccess(true);
      setFormData(emptyForm);
    } catch (err) {
      setError(err.message || "An unexpected error occurred.");
    } finally {
      setLoading(false);
    }
  }

  return (
    <>
      <div className="auth-panel-heading">
        <span className="auth-panel-kicker">JOIN THE COMMUNITY</span>
        <h2 id="auth-register-heading">Create Account</h2>
        <p>Join NLIC Connect and become part of our church community.</p>
      </div>
      {success ? (
        <div role="status" className="auth-alert auth-alert-success">
          <h3>Registration Successful!</h3>
          <p>Your NLIC Connect account has been created. You can now sign in with your email and password.</p>
          <Link to="/login">Continue to Member Login</Link>
        </div>
      ) : (
        <form onSubmit={handleSubmit} className="auth-form">
          <div className="auth-name-grid">
            <label htmlFor="auth-register-first-name">First Name
              <input id="auth-register-first-name" name="firstName" type="text" maxLength={50}
                autoComplete="given-name" value={formData.firstName} onChange={handleChange}
                placeholder="Enter your first name" required />
            </label>
            <label htmlFor="auth-register-last-name">Last Name
              <input id="auth-register-last-name" name="lastName" type="text" maxLength={50}
                autoComplete="family-name" value={formData.lastName} onChange={handleChange}
                placeholder="Enter your last name" required />
            </label>
          </div>
          <label htmlFor="auth-register-email">Email Address
            <input id="auth-register-email" name="email" type="email" maxLength={100}
              autoComplete="email" value={formData.email} onChange={handleChange}
              placeholder="Enter your email" required />
          </label>
          <label htmlFor="auth-register-password">Password
            <input id="auth-register-password" name="password" type="password" minLength={12}
              maxLength={64} autoComplete="new-password" value={formData.password}
              onChange={handleChange} placeholder="Create a password" required />
            <small>Use 12–64 characters.</small>
          </label>
          <label htmlFor="auth-register-confirm-password">Confirm Password
            <input id="auth-register-confirm-password" name="confirmPassword" type="password"
              minLength={12} maxLength={64} autoComplete="new-password"
              value={formData.confirmPassword} onChange={handleChange}
              placeholder="Confirm your password" required />
          </label>
          {error && <p role="alert" className="auth-alert auth-alert-error">{error}</p>}
          <button className="auth-submit" type="submit" disabled={loading}>
            {loading ? "Creating Account..." : "Create Account"}
          </button>
        </form>
      )}
      <p className="auth-panel-foot">Already have an account? <Link to="/login">Sign in here.</Link></p>
    </>
  );
}
