
import { BrowserRouter, Routes, Route } from "react-router-dom";

import Layout from "./components/Layout";
import ProtectedRoute from "./components/ProtectedRoute";

import Dashboard from "./pages/Dashboard";
import Events from "./pages/Events";
import PrayerRequests from "./pages/PrayerRequests";
import Volunteer from "./pages/Volunteer";
import NewLifePath from "./pages/NewLifePath";
import Login from "./pages/Login";
import Register from "./pages/Register";

import "./App.css";

/**
 * Defines the main routing structure and authentication
 * protection for NLIC Connect.
 *
 * Includes public login and registration pages,
 * while protecting authenticated application pages.
 */
function App() {
  return (
    <BrowserRouter>
      <Routes>

        {/* Public authentication pages */}
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />

        {/* Authentication required */}
        <Route element={<ProtectedRoute />}>

          {/* Shared application layout */}
          <Route element={<Layout />}>
            <Route path="/" element={<Dashboard />} />
            <Route path="/events" element={<Events />} />
            <Route path="/prayer" element={<PrayerRequests />} />
            <Route path="/volunteer" element={<Volunteer />} />
            <Route
              path="/new-life-path"
              element={<NewLifePath />}
            />
          </Route>

        </Route>

      </Routes>
    </BrowserRouter>
  );
}

export default App;