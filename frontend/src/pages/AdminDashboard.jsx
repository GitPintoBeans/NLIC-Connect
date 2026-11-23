import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import "./AdminDashboard.css";

const API = "http://localhost:8080";

/** Small neutral icons that match the original Milestone 3 dashboard wireframe. */
function DashboardIcon({ name, size = 23 }) {
  const common = { width: size, height: size, viewBox: "0 0 24 24", fill: "none", stroke: "currentColor", strokeWidth: 1.8, strokeLinecap: "round", strokeLinejoin: "round", "aria-hidden": true };
  const paths = {
    members: <><circle cx="9" cy="8" r="3"/><path d="M3 20v-2a6 6 0 0 1 12 0v2H3z"/><circle cx="18" cy="9" r="2"/><path d="M17 15a4 4 0 0 1 4 4v1h-3"/></>,
    events: <><rect x="3" y="5" width="18" height="16" rx="2"/><path d="M7 3v4M17 3v4M3 10h18M8 14h2M14 14h2M8 18h2"/></>,
    prayer: <><path d="m8 3 4 6 4-6 3 6-3 12h-8L5 9z"/><path d="M12 9v12M8 14l4-2 4 2"/></>,
    volunteer: <><circle cx="8" cy="7" r="2.5"/><circle cx="17" cy="7" r="2.5"/><path d="M2 20v-3a6 6 0 0 1 12 0v3M12 15a6 6 0 0 1 10 3v2"/></>,
    path: <><path d="M5 21V4h11l-2.5 4L16 12H5"/><path d="M5 21h14"/></>,
    activity: <><path d="M4 19h16M6 16V9M12 16V4M18 16v-5"/></>,
    check: <><path d="m5 12 5 5L20 7"/></>,
    refresh: <><path d="M20 11a8 8 0 0 0-14-5L3 9M3 3v6h6M4 13a8 8 0 0 0 14 5l3-3m0 6v-6h-6"/></>,
  };
  return <svg {...common}>{paths[name] || paths.activity}</svg>;
}

function displayDate(date) {
  if (!date) return "Date to be announced";
  const parsed = new Date(date);
  return Number.isNaN(parsed.getTime()) ? date : parsed.toLocaleDateString("en-US", { month: "short", day: "numeric", year: "numeric" });
}

function displayTime(date) {
  if (!date) return "";
  const parsed = new Date(date);
  return Number.isNaN(parsed.getTime()) ? "" : parsed.toLocaleTimeString("en-US", { hour: "numeric", minute: "2-digit" });
}

const actions = [
  { to: "/manage/events", icon: "events", label: "Manage Events" },
  { to: "/prayer", icon: "prayer", label: "View Prayer Requests" },
  { to: "/volunteer", icon: "volunteer", label: "Volunteer Opportunities" },
  { to: "/new-life-path", icon: "path", label: "New Life Path" },
];

/** Role-protected, read-only reporting modeled on Figure 13 of the approved design. */
export default function AdminDashboard() {
  const [summary, setSummary] = useState(null);
  const [status, setStatus] = useState("loading");
  const [error, setError] = useState("");

  async function requestSummary(signal) {
    const response = await fetch(`${API}/api/admin/dashboard/summary`, {
      method: "GET", credentials: "include", signal,
    });
    if (response.status === 401 || response.status === 403) {
      throw new Error("Administrator access is required to view this dashboard.");
    }
    if (!response.ok) throw new Error("Unable to retrieve the administrator summary.");
    return response.json();
  }

  useEffect(() => {
    const controller = new AbortController();
    requestSummary(controller.signal)
      .then((data) => {
        if (!controller.signal.aborted) { setSummary(data); setStatus("ready"); }
      })
      .catch((err) => {
        if (!controller.signal.aborted) { setError(err.message); setStatus("error"); }
      });
    return () => controller.abort();
  }, []);

  async function refresh() {
    setStatus("loading"); setError("");
    try { setSummary(await requestSummary()); setStatus("ready"); }
    catch (err) { setError(err.message); setStatus("error"); }
  }

  const s = summary || {};
  const prayerTotal = s.prayerRequestsTotal ?? 0;
  const activePrayers = s.activePrayerRequests ?? 0;
  const activePrayerPct = prayerTotal ? Math.round((activePrayers / prayerTotal) * 100) : 0;
  const participantPct = s.activeAccounts ? Math.min(100, Math.round(((s.newLifePathParticipants ?? 0) / s.activeAccounts) * 100)) : 0;
  const cards = [
    { key: "activeAccounts", title: "Active Accounts", icon: "members", note: "Active member and staff accounts" },
    { key: "upcomingEvents", title: "Upcoming Events", icon: "events", note: "Scheduled after today" },
    { key: "prayerRequestsTotal", title: "Prayer Requests", icon: "prayer", note: `${activePrayers} currently active` },
    { key: "activeVolunteerSignups", title: "Volunteer Signups", icon: "volunteer", note: "Currently signed-up records" },
  ];

  return (
    <main className="admin-dashboard">
      <div className="admin-dashboard-shell">
        <aside className="admin-dashboard-sidebar" aria-label="Administrative dashboard navigation">
          <div className="admin-dashboard-sidebar-brand">NLIC CONNECT <small>CHURCH ADMINISTRATION</small></div>
          <Link className="admin-side-link selected" to="/admin/dashboard"><DashboardIcon name="activity" size={18}/> Dashboard</Link>
          <Link className="admin-side-link" to="/manage/events"><DashboardIcon name="events" size={18}/> Events</Link>
          <Link className="admin-side-link" to="/prayer"><DashboardIcon name="prayer" size={18}/> Prayer Requests</Link>
          <Link className="admin-side-link" to="/new-life-path"><DashboardIcon name="path" size={18}/> New Life Path</Link>
          <Link className="admin-side-link" to="/volunteer"><DashboardIcon name="volunteer" size={18}/> Volunteer</Link>
          <div className="admin-dashboard-sidebar-foot">Administrative overview<br/>NLIC Connect · Milestone 5</div>
        </aside>

        <div className="admin-dashboard-main">
          <header className="admin-dashboard-header">
            <div>
              <p className="admin-dashboard-eyebrow">NLIC CONNECT · CHURCH LEADERSHIP</p>
              <h1>Admin Dashboard</h1>
              <p>Manage members, events, prayer requests, and ministry engagement.</p>
            </div>
            <button className="admin-refresh" onClick={refresh} type="button" disabled={status === "loading"}>
              <DashboardIcon name="refresh" size={16}/> {status === "loading" ? "Loading..." : "Refresh"}
            </button>
          </header>

          {status === "loading" && <p className="admin-dashboard-notice" role="status">Loading live dashboard statistics...</p>}
          {error && <p className="admin-dashboard-error" role="alert">{error}</p>}

          {summary && status === "ready" && (
            <>
              <section className="admin-dashboard-metrics" aria-label="Church statistics">
                {cards.map((card) => <article className="admin-dashboard-metric" key={card.key}>
                  <span className="admin-metric-icon"><DashboardIcon name={card.icon} size={30}/></span>
                  <div><h2>{card.title}</h2><p className="admin-dashboard-value">{s[card.key] ?? 0}</p><p className="admin-dashboard-note">{card.note}</p></div>
                </article>)}
              </section>

              <div className="admin-dashboard-panels">
                <section className="admin-dashboard-panel" aria-labelledby="admin-engagement-heading">
                  <div className="admin-panel-heading"><h2 id="admin-engagement-heading">Engagement Overview</h2><span>Current totals</span></div>
                  <div className="admin-activity-row"><span className="admin-activity-icon"><DashboardIcon name="events" size={19}/></span><div><strong>Event RSVPs</strong><small>Active registrations across all events</small></div><b>{s.registeredEventRsvps ?? 0}</b></div>
                  <div className="admin-activity-row"><span className="admin-activity-icon"><DashboardIcon name="volunteer" size={19}/></span><div><strong>Volunteer opportunities</strong><small>Active upcoming opportunities</small></div><b>{s.upcomingVolunteerOpportunities ?? 0}</b></div>
                  <div className="admin-activity-row"><span className="admin-activity-icon"><DashboardIcon name="path" size={19}/></span><div><strong>New Life Path participants</strong><small>Accounts with a completed step</small></div><b>{s.newLifePathParticipants ?? 0}</b></div>
                </section>

                <section className="admin-dashboard-panel" aria-labelledby="admin-prayers-heading">
                  <div className="admin-panel-heading"><h2 id="admin-prayers-heading">Prayer Requests Overview</h2><Link to="/prayer">View All →</Link></div>
                  <div className="admin-prayer-overview">
                    <div className="admin-prayer-ring" role="img" aria-label={`${activePrayers} active prayer requests out of ${prayerTotal} total`} style={{ "--prayer-share": `${activePrayerPct}%` }}><div><strong>{prayerTotal}</strong><span>Total</span></div></div>
                    <div className="admin-prayer-legend"><div><span className="legend-dot active"/>Active <b>{activePrayers}</b></div><div><span className="legend-dot other"/>Other statuses <b>{Math.max(0, prayerTotal - activePrayers)}</b></div></div>
                  </div>
                  <p className="admin-panel-caption">Counts only. Private prayer text is never included in dashboard reports.</p>
                </section>

                <section className="admin-dashboard-panel" aria-labelledby="admin-modules-heading">
                  <div className="admin-panel-heading"><h2 id="admin-modules-heading">Church Modules</h2><span>Available pages</span></div>
                  <div className="admin-module-row"><DashboardIcon name="events" size={17}/> Event management <Link to="/manage/events">Open →</Link></div>
                  <div className="admin-module-row"><DashboardIcon name="prayer" size={17}/> Prayer requests <Link to="/prayer">Open →</Link></div>
                  <div className="admin-module-row"><DashboardIcon name="volunteer" size={17}/> Volunteer opportunities <Link to="/volunteer">Open →</Link></div>
                  <div className="admin-module-row"><DashboardIcon name="path" size={17}/> New Life Path <Link to="/new-life-path">Open →</Link></div>
                  <p className="admin-panel-caption">Links indicate available pages, not infrastructure uptime.</p>
                </section>

                <section className="admin-dashboard-panel" aria-labelledby="admin-events-heading">
                  <div className="admin-panel-heading"><h2 id="admin-events-heading">Upcoming Events</h2><Link to="/manage/events">View All →</Link></div>
                  {!s.nextEvents?.length ? <p className="admin-empty">No upcoming events are scheduled.</p> :
                    <div className="admin-table-scroll"><table className="admin-dashboard-table"><thead><tr><th>Date</th><th>Event</th><th>Location</th></tr></thead><tbody>
                      {s.nextEvents.map((event) => <tr key={event.eventId}><td><strong>{displayDate(event.eventDate)}</strong><small>{displayTime(event.eventDate)}</small></td><td>{event.title}</td><td>{event.location || "TBA"}</td></tr>)}
                    </tbody></table></div>}
                </section>

                <section className="admin-dashboard-panel" aria-labelledby="admin-path-heading">
                  <div className="admin-panel-heading"><h2 id="admin-path-heading">New Life Path Participation</h2><Link to="/new-life-path">View Path →</Link></div>
                  <div className="admin-path-total"><strong>{s.newLifePathParticipants ?? 0}</strong><span>accounts with one or more completed steps</span></div>
                  <div className="admin-progress-label"><span>Share of active accounts participating</span><strong>{participantPct}%</strong></div>
                  <div className="admin-progress-track" role="progressbar" aria-valuenow={participantPct} aria-valuemin={0} aria-valuemax={100} aria-label="Share of active accounts with a completed New Life Path step"><div style={{ width: `${participantPct}%` }}/></div>
                  <p className="admin-panel-caption">Participation tracks activities only. It is not a ranking of members' faith.</p>
                </section>

                <section className="admin-dashboard-panel" aria-labelledby="admin-actions-heading">
                  <div className="admin-panel-heading"><h2 id="admin-actions-heading">Quick Actions</h2></div>
                  <div className="admin-quick-grid">{actions.map((action) => <Link to={action.to} key={action.to} className="admin-quick-action"><DashboardIcon name={action.icon} size={25}/><span>{action.label}</span></Link>)}</div>
                </section>
              </div>
            </>
          )}
        </div>
      </div>
    </main>
  );
}
