import { BrowserRouter, Routes, Route } from "react-router-dom";
import Layout from "./components/Layout";
import Dashboard from "./pages/Dashboard";
import Events from "./pages/Events";
import PrayerRequests from "./pages/PrayerRequests";
import Volunteer from "./pages/Volunteer";
import NewLifePath from "./pages/NewLifePath";
import "./App.css";

/**
 * Defines the main routing structure for NLIC Connect.
 */
function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route element={<Layout />}>
          <Route path="/" element={<Dashboard />} />
          <Route path="/events" element={<Events />} />
          <Route path="/prayer" element={<PrayerRequests />} />
          <Route path="/volunteer" element={<Volunteer />} />
          <Route path="/new-life-path" element={<NewLifePath />} />
        </Route>
      </Routes>
    </BrowserRouter>
  );
}

export default App;