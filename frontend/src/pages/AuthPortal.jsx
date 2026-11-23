import { Link, NavLink, useLocation } from "react-router-dom";
import Login from "./Login";
import Register from "./Register";
import logo from "../assets/nlic-logo.png";
import "./AuthPortal.css";

/** Public entry screen based on the approved Figure 13.a wireframe. */
export default function AuthPortal() {
  const { pathname } = useLocation();
  const registrationFirst = pathname === "/register";

  return (
    <div className="auth-portal">
      <header className="auth-site-header">
        <Link className="auth-brand" to="/login" aria-label="NLIC Connect sign in">
          <span className="auth-brand-image"><img src={logo} alt="New Life in Christ Ministries logo" /></span>
          <span className="auth-brand-copy">
            <strong>NLIC Connect</strong>
            <small>NEW LIFE IN CHRIST MINISTRIES</small>
          </span>
        </Link>
        <nav className="auth-top-nav" aria-label="Account navigation">
          <NavLink to="/login" className={({ isActive }) => isActive ? "auth-nav-link current" : "auth-nav-link"}>Login</NavLink>
          <NavLink to="/register" className={({ isActive }) => isActive ? "auth-nav-link current" : "auth-nav-link"}>Register</NavLink>
        </nav>
      </header>

      <main className="auth-main">
        <div className="auth-intro">
          <span className="auth-kicker">NLIC CONNECT · OUR CHURCH COMMUNITY</span>
          <h1>Connect. Grow. Serve.</h1>
          <p>One place for fellowship, upcoming events, prayer, opportunities to serve, and your New Life Path.</p>
        </div>
        <div className={`auth-columns${registrationFirst ? " auth-register-first" : ""}`}>
          <section className="auth-panel auth-login-panel" aria-labelledby="auth-login-heading">
            <Login />
          </section>
          <section className="auth-panel auth-register-panel" aria-labelledby="auth-register-heading">
            <Register />
          </section>
        </div>
      </main>

      <footer className="auth-site-footer">
        <div>
          <strong>NLIC Connect</strong>
          <span>New Life in Christ Ministries</span>
        </div>
        <p>Connecting People <span aria-hidden="true">|</span> Growing Together <span aria-hidden="true">|</span> Making a Difference</p>
      </footer>
    </div>
  );
}
