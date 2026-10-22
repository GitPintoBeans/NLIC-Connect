
import { useEffect, useState } from "react";
import { NavLink, useNavigate } from "react-router-dom";
import logo from "../assets/nlic-logo.png";

/**
 * Provides navigation and authenticated member information
 * for the NLIC Connect application.
 */
function Navbar() {
  const navigate = useNavigate();

  const [member, setMember] = useState(null);
  const [loading, setLoading] = useState(true);
  const [loggingOut, setLoggingOut] = useState(false);
  const [logoutError, setLogoutError] = useState("");

  const API_BASE_URL = "http://localhost:8080";

  const getLinkClass = ({ isActive }) =>
    isActive ? "nav-link active" : "nav-link";

  // Retrieve the currently authenticated member.
  useEffect(() => {
    const controller = new AbortController();

    async function loadMember() {
      try {
        const response = await fetch(
          `${API_BASE_URL}/api/auth/me`,
          {
            method: "GET",
            credentials: "include",
            signal: controller.signal,
          }
        );

        if (!response.ok) {
          throw new Error("No authenticated session.");
        }

        const data = await response.json();
        setMember(data);
      } catch (error) {
        if (error.name !== "AbortError") {
          console.error("Member lookup failed:", error);
          setMember(null);
        }
      } finally {
        if (!controller.signal.aborted) {
          setLoading(false);
        }
      }
    }

    loadMember();

    return () => controller.abort();
  }, []);

  // Display the member's actual role.
  function getRoleLabel(roles = []) {
    if (roles.includes("ROLE_ADMIN")) {
      return "Administrator";
    }

    if (roles.includes("ROLE_LEADER")) {
      return "Ministry Leader";
    }

    if (roles.includes("ROLE_MEMBER")) {
      return "Member";
    }

    return "Unknown Role";
  }

  // Generate an avatar initial.
  function getInitials(email) {
    if (!email) {
      return "?";
    }

    return email.charAt(0).toUpperCase();
  }

  // Securely log the member out.
  async function handleLogout() {
    setLoggingOut(true);
    setLogoutError("");

    try {
      // Get a CSRF token associated with the session.
      const csrfResponse = await fetch(
        `${API_BASE_URL}/api/auth/csrf`,
        {
          method: "GET",
          credentials: "include",
        }
      );

      if (!csrfResponse.ok) {
        throw new Error("Unable to prepare secure logout.");
      }

      const csrf = await csrfResponse.json();

      // Send the logout request to Spring Security.
      const logoutResponse = await fetch(
        `${API_BASE_URL}/api/auth/logout`,
        {
          method: "POST",
          credentials: "include",
          headers: {
            [csrf.headerName]: csrf.token,
          },
        }
      );

      if (!logoutResponse.ok) {
        throw new Error("Logout failed. Please try again.");
      }

      // Clear member information and return to login.
      setMember(null);
      navigate("/login", { replace: true });

    } catch (error) {
      setLogoutError(error.message);
    } finally {
      setLoggingOut(false);
    }
  }

  return (
    <header className="navbar">
      <div className="brand">
        <img
          src={logo}
          alt="New Life in Christ Ministries"
          className="brand-logo"
        />

        <div className="brand-text">
          <strong>NEW LIFE IN CHRIST</strong>
          <span>MINISTRIES</span>
        </div>
      </div>

      <nav className="nav-links">
        <NavLink to="/" end className={getLinkClass}>
          HOME
        </NavLink>

        <NavLink to="/events" className={getLinkClass}>
          EVENTS
        </NavLink>

        <NavLink to="/prayer" className={getLinkClass}>
          PRAYER
        </NavLink>

        <NavLink to="/volunteer" className={getLinkClass}>
          VOLUNTEER
        </NavLink>

        <NavLink to="/new-life-path" className={getLinkClass}>
          NEW LIFE PATH
        </NavLink>
      </nav>

      <div className="member-badge">
        <span className="member-avatar">
          {getInitials(member?.email)}
        </span>

        <div>
          <strong>
            {loading
              ? "Loading..."
              : member
              ? member.email
              : "Not Signed In"}
          </strong>

          <small>
            {member
              ? getRoleLabel(member.roles)
              : "Guest"}
          </small>
        </div>
      </div>

      <div>
        <button
          type="button"
          onClick={handleLogout}
          disabled={loggingOut}
          style={{
            backgroundColor: "transparent",
            border: "1px solid #d9a441",
            borderRadius: "6px",
            color: "#d9a441",
            padding: "10px 16px",
            cursor: loggingOut ? "wait" : "pointer",
            fontWeight: "600",
          }}
        >
          {loggingOut ? "Logging Out..." : "Log Out"}
        </button>

        {logoutError && (
          <p role="alert" style={{ color: "#ff8080" }}>
            {logoutError}
          </p>
        )}
      </div>
    </header>
  );
}

export default Navbar;
