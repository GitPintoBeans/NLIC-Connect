
import { useState } from "react";
import { Link } from "react-router-dom";

/**
 * Member registration page for NLIC Connect.
 *
 * Creates new member accounts through the Spring Boot
 * registration API using CSRF-protected requests.
 */
function Register() {
  const [formData, setFormData] = useState({
    firstName: "",
    lastName: "",
    email: "",
    password: "",
    confirmPassword: "",
  });

  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState(false);

  const API_BASE_URL = "http://localhost:8080";

  function handleChange(event) {
    const { name, value } = event.target;

    setFormData((previous) => ({
      ...previous,
      [name]: value,
    }));
  }

  async function handleSubmit(event) {
    event.preventDefault();

    setError("");
    setSuccess(false);

    // Confirm both passwords match.
    if (formData.password !== formData.confirmPassword) {
      setError("Passwords do not match.");
      return;
    }

    // Match the backend password requirements.
    if (
      formData.password.length < 12 ||
      formData.password.length > 64
    ) {
      setError(
        "Password must contain between 12 and 64 characters."
      );
      return;
    }

    setLoading(true);

    try {
      // Retrieve CSRF token and session cookie.
      const csrfResponse = await fetch(
        `${API_BASE_URL}/api/auth/csrf`,
        {
          method: "GET",
          credentials: "include",
        }
      );

      if (!csrfResponse.ok) {
        throw new Error(
          "Unable to initialize secure registration."
        );
      }

      const csrf = await csrfResponse.json();

      // Prepare registration information.
      const requestBody = {
        firstName: formData.firstName.trim(),
        lastName: formData.lastName.trim(),
        email: formData.email.trim().toLowerCase(),
        password: formData.password,
      };

      // Submit registration to Spring Boot.
      const response = await fetch(
        `${API_BASE_URL}/api/auth/register`,
        {
          method: "POST",
          credentials: "include",
          headers: {
            "Content-Type": "application/json",
            [csrf.headerName]: csrf.token,
          },
          body: JSON.stringify(requestBody),
        }
      );

      if (!response.ok) {
        if (response.status === 409) {
          throw new Error(
            "An account with this email address already exists."
          );
        }

        if (response.status === 400) {
          throw new Error(
            "Please check your information and try again."
          );
        }

        throw new Error(
          "Registration failed. Please try again."
        );
      }

      // Account created successfully.
      setSuccess(true);

      // Clear sensitive information from the form.
      setFormData({
        firstName: "",
        lastName: "",
        email: "",
        password: "",
        confirmPassword: "",
      });

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
        maxWidth: "460px",
        margin: "60px auto",
        padding: "32px",
      }}
    >
      <h1>NLIC Connect</h1>

      <h2>Create Your Member Account</h2>

      <p>
        Welcome to New Life in Christ Ministries!
        Create your account to connect with your
        church family and begin your New Life Path.
      </p>

      {success ? (
        <div role="status">
          <h3>Registration Successful!</h3>

          <p>
            Your NLIC Connect account has been created.
            You can now sign in using your email
            address and password.
          </p>

          <Link to="/login">
            Continue to Member Login
          </Link>
        </div>
      ) : (
        <form onSubmit={handleSubmit}>

          <div style={{ marginBottom: "16px" }}>
            <label htmlFor="firstName">
              First Name
            </label>

            <input
              id="firstName"
              name="firstName"
              type="text"
              value={formData.firstName}
              onChange={handleChange}
              maxLength={50}
              autoComplete="given-name"
              required
              style={inputStyle}
            />
          </div>

          <div style={{ marginBottom: "16px" }}>
            <label htmlFor="lastName">
              Last Name
            </label>

            <input
              id="lastName"
              name="lastName"
              type="text"
              value={formData.lastName}
              onChange={handleChange}
              maxLength={50}
              autoComplete="family-name"
              required
              style={inputStyle}
            />
          </div>

          <div style={{ marginBottom: "16px" }}>
            <label htmlFor="email">
              Email Address
            </label>

            <input
              id="email"
              name="email"
              type="email"
              value={formData.email}
              onChange={handleChange}
              maxLength={100}
              autoComplete="email"
              required
              style={inputStyle}
            />
          </div>

          <div style={{ marginBottom: "16px" }}>
            <label htmlFor="password">
              Password
            </label>

            <input
              id="password"
              name="password"
              type="password"
              value={formData.password}
              onChange={handleChange}
              minLength={12}
              maxLength={64}
              autoComplete="new-password"
              required
              style={inputStyle}
            />

            <small>
              Password must contain between
              12 and 64 characters.
            </small>
          </div>

          <div style={{ marginBottom: "20px" }}>
            <label htmlFor="confirmPassword">
              Confirm Password
            </label>

            <input
              id="confirmPassword"
              name="confirmPassword"
              type="password"
              value={formData.confirmPassword}
              onChange={handleChange}
              minLength={12}
              maxLength={64}
              autoComplete="new-password"
              required
              style={inputStyle}
            />
          </div>

          {error && (
            <p role="alert" style={{ color: "crimson" }}>
              {error}
            </p>
          )}

          <button
            type="submit"
            disabled={loading}
          >
            {loading
              ? "Creating Account..."
              : "Create Account"}
          </button>

        </form>
      )}

      {!success && (
        <p style={{ marginTop: "24px" }}>
          Already have an account?{" "}
          <Link to="/login">Sign In</Link>
        </p>
      )}
    </main>
  );
}

const inputStyle = {
  display: "block",
  width: "100%",
  padding: "10px",
  marginTop: "6px",
  boxSizing: "border-box",
};

export default Register;