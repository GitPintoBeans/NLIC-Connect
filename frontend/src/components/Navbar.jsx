import { NavLink } from "react-router-dom";
import logo from "../assets/nlic-logo.png";

/**
 * Provides primary navigation between NLIC Connect modules.
 */
function Navbar() {
  const getLinkClass = ({ isActive }) =>
    isActive ? "nav-link active" : "nav-link";

  return (
    <header className="navbar">
      <div className="brand">
        <img src={logo} alt="New Life in Christ Ministries" className="brand-logo" />

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
        <span className="member-avatar">JP</span>
        <div>
          <strong>James Pinto</strong>
          <small>Administrator</small>
        </div>
      </div>
    </header>
  );
}

export default Navbar;