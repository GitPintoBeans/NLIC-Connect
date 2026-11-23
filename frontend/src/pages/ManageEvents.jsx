import { useEffect, useState } from "react";
import "./ManageEvents.css";

const API = "http://localhost:8080";
const emptyForm = {
  title: "",
  description: "",
  eventDate: "",
  location: "",
  capacity: "",
  registrationOpen: true,
};

/** Event management for authenticated NLIC leaders and administrators. */
export default function ManageEvents() {
  const [authorized, setAuthorized] = useState(null);
  const [events, setEvents] = useState([]);
  const [form, setForm] = useState(emptyForm);
  const [editingId, setEditingId] = useState(null);
  const [selectedRosterId, setSelectedRosterId] = useState(null);
  const [roster, setRoster] = useState([]);
  const [busy, setBusy] = useState(false);
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");

  useEffect(() => {
    let cancelled = false;
    async function initialize() {
      try {
        const meResponse = await fetch(`${API}/api/auth/me`, { credentials: "include" });
        if (!meResponse.ok) throw new Error("Please sign in again.");
        const me = await meResponse.json();
        const canManage = me.roles?.some((role) =>
          ["ROLE_ADMIN", "ROLE_LEADER"].includes(role)
        );
        if (!cancelled) setAuthorized(Boolean(canManage));
        if (!canManage) return;
        const eventResponse = await fetch(`${API}/api/admin/events`, {
          credentials: "include",
        });
        if (!eventResponse.ok) throw new Error("Unable to load managed events.");
        const eventData = await eventResponse.json();
        if (!cancelled) setEvents(eventData);
      } catch (err) {
        if (!cancelled) {
          setAuthorized(false);
          setError(err.message);
        }
      }
    }
    initialize();
    return () => { cancelled = true; };
  }, []);

  async function parseResponse(response) {
    if (response.ok) {
      return response.status === 204 ? null : response.json();
    }
    const body = await response.json().catch(() => ({}));
    throw new Error(body.message || `Request failed (HTTP ${response.status}).`);
  }

  async function secureRequest(path, method, payload) {
    const tokenResponse = await fetch(`${API}/api/auth/csrf`, {
      credentials: "include",
    });
    if (!tokenResponse.ok) throw new Error("Unable to initialize secure request.");
    const csrf = await tokenResponse.json();
    return parseResponse(await fetch(`${API}${path}`, {
      method,
      credentials: "include",
      headers: {
        [csrf.headerName]: csrf.token,
        ...(payload ? { "Content-Type": "application/json" } : {}),
      },
      ...(payload ? { body: JSON.stringify(payload) } : {}),
    }));
  }

  async function refreshEvents() {
    const response = await fetch(`${API}/api/admin/events`, { credentials: "include" });
    setEvents(await parseResponse(response));
  }

  function edit(event) {
    setEditingId(event.eventId);
    setSelectedRosterId(null);
    setError("");
    setMessage("");
    setForm({
      title: event.title || "",
      description: event.description || "",
      eventDate: (event.eventDate || "").slice(0, 16),
      location: event.location || "",
      capacity: event.capacity == null ? "" : String(event.capacity),
      registrationOpen: event.registrationOpen === true,
    });
    window.scrollTo({ top: 0, behavior: "smooth" });
  }

  function resetForm() {
    setForm(emptyForm);
    setEditingId(null);
  }

  async function save(event) {
    event.preventDefault();
    setError("");
    setMessage("");
    setBusy(true);
    try {
      const payload = {
        title: form.title.trim(),
        description: form.description.trim(),
        eventDate: `${form.eventDate}:00`,
        location: form.location.trim(),
        capacity: form.capacity === "" ? null : Number(form.capacity),
        registrationOpen: form.registrationOpen,
      };
      const method = editingId ? "PUT" : "POST";
      await secureRequest(editingId
        ? `/api/admin/events/${editingId}` : "/api/admin/events", method, payload);
      setMessage(editingId ? "Event updated successfully." : "Event created successfully.");
      resetForm();
      await refreshEvents();
    } catch (err) {
      setError(err.message);
    } finally {
      setBusy(false);
    }
  }

  async function remove(event) {
    const confirmed = window.confirm(
      `Delete ${event.title}? This is only allowed when no RSVP history exists.`
    );
    if (!confirmed) return;
    setError("");
    setMessage("");
    setBusy(true);
    try {
      await secureRequest(`/api/admin/events/${event.eventId}`, "DELETE");
      setMessage("Event deleted.");
      if (editingId === event.eventId) resetForm();
      if (selectedRosterId === event.eventId) setSelectedRosterId(null);
      await refreshEvents();
    } catch (err) {
      setError(err.message);
    } finally {
      setBusy(false);
    }
  }

  async function showRoster(eventId) {
    setError("");
    setSelectedRosterId(null);
    try {
      const response = await fetch(`${API}/api/event-registrations/event/${eventId}`, {
        credentials: "include",
      });
      setRoster(await parseResponse(response));
      setSelectedRosterId(eventId);
    } catch (err) {
      setError(err.message);
    }
  }

  if (authorized === null) {
    return <main className="manage-events"><p>Checking event-management access...</p></main>;
  }
  if (!authorized) {
    return <main className="manage-events"><h1>Event Management</h1>
      <p role="alert">{error || "Only authorized leaders and administrators can manage events."}</p>
    </main>;
  }

  return (
    <main className="manage-events">
      <div className="manage-events-header">
        <p className="manage-eyebrow">NLIC CONNECT • CHURCH LEADERSHIP</p>
        <h1>Event Management</h1>
        <p>Create, update, close registration, or review RSVPs for church events.</p>
      </div>
      {error && <p role="alert" className="manage-notice manage-error">{error}</p>}
      {message && <p role="status" className="manage-notice manage-success">{message}</p>}

      <section className="manage-panel" aria-labelledby="manage-form-heading">
        <h2 id="manage-form-heading">{editingId ? `Edit Event #${editingId}` : "Create a New Event"}</h2>
        <form onSubmit={save} className="manage-form">
          <label>Event title *
            <input type="text" required maxLength={100} value={form.title}
              onChange={(e) => setForm({ ...form, title: e.target.value })} />
          </label>
          <label>Event date and time *
            <input type="datetime-local" required value={form.eventDate}
              onChange={(e) => setForm({ ...form, eventDate: e.target.value })} />
          </label>
          <label>Location
            <input type="text" maxLength={255} value={form.location}
              onChange={(e) => setForm({ ...form, location: e.target.value })} />
          </label>
          <label>Capacity (blank = unlimited)
            <input type="number" min="1" value={form.capacity}
              onChange={(e) => setForm({ ...form, capacity: e.target.value })} />
          </label>
          <label className="manage-full-width">Description
            <textarea rows="3" value={form.description}
              onChange={(e) => setForm({ ...form, description: e.target.value })} />
          </label>
          <label className="manage-checkbox">
            <input type="checkbox" checked={form.registrationOpen}
              onChange={(e) => setForm({ ...form, registrationOpen: e.target.checked })} />
            Registration open
          </label>
          <div className="manage-actions">
            <button className="manage-primary" type="submit" disabled={busy}>
              {busy ? "Saving..." : editingId ? "Save Changes" : "Create Event"}
            </button>
            {editingId && <button type="button" onClick={resetForm} disabled={busy}>Cancel Editing</button>}
          </div>
        </form>
      </section>

      <section className="manage-panel" aria-labelledby="manage-list-heading">
        <h2 id="manage-list-heading">Existing Events</h2>
        {events.length === 0 ? <p>No events are available yet.</p> : (
          <div className="manage-table-wrap">
            <table className="manage-table">
              <thead><tr><th>Event</th><th>Date</th><th>Capacity</th><th>Registration</th><th>Actions</th></tr></thead>
              <tbody>
                {events.map((event) => <tr key={event.eventId}>
                  <td>{event.title}</td>
                  <td>{(event.eventDate || "").replace("T", " ").slice(0, 16)}</td>
                  <td>{event.capacity ?? "Unlimited"}</td>
                  <td>{event.registrationOpen ? "Open" : "Closed"}</td>
                  <td><div className="manage-row-actions">
                    <button type="button" onClick={() => edit(event)}>Edit</button>
                    <button type="button" onClick={() => showRoster(event.eventId)}>RSVPs</button>
                    <button type="button" disabled={busy} onClick={() => remove(event)}>Delete</button>
                  </div></td>
                </tr>)}
              </tbody>
            </table>
          </div>
        )}
      </section>

      {selectedRosterId !== null && <section className="manage-panel" aria-live="polite">
        <h2>RSVPs for Event #{selectedRosterId}</h2>
        <p>Only authorized church leadership can view these participation records.</p>
        {roster.length === 0 ? <p>No RSVP history for this event.</p> : (
          <div className="manage-table-wrap"><table className="manage-table">
            <thead><tr><th>Registration</th><th>Member ID</th><th>Status</th><th>Registered</th></tr></thead>
            <tbody>{roster.map((item) => <tr key={item.registrationId}>
              <td>{item.registrationId}</td><td>{item.userId}</td>
              <td>{item.status}</td><td>{(item.registrationDate || "").replace("T", " ").slice(0, 16)}</td>
            </tr>)}</tbody>
          </table></div>
        )}
      </section>}
    </main>
  );
}
