import { Outlet } from "react-router-dom";
import Navbar from "./Navbar";

/**
 * Provides the shared page structure used throughout NLIC Connect.
 */
function Layout() {
  return (
    <div className="app-shell">
      <Navbar />

      <main className="main-content">
        <Outlet />
      </main>

      <footer className="footer">
        <p>New Life in Christ Ministries</p>
        <span>NLIC Connect • Connecting Faith, Community, and Service</span>
      </footer>
    </div>
  );
}

export default Layout;