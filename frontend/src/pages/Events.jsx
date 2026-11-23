import { useEffect, useState } from "react";

/**
 * Displays upcoming church events retrieved from the
 * NLIC Connect Spring Boot REST API.
 */
function Events() {
  const [events, setEvents] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [registrations, setRegistrations] = useState([]);
  const [registrationMessage, setRegistrationMessage] = useState("");
  const [registrationError, setRegistrationError] = useState("");
  const [submittingEventId, setSubmittingEventId] = useState(null);

  /**
   * Retrieves event records from the backend when the page loads.
   */
  useEffect(() => {
    fetch("http://localhost:8080/api/events")
      .then((response) => {
        if (!response.ok) {
          throw new Error("Unable to retrieve events.");
        }

        return response.json();
      })
      .then((data) => {
        setEvents(data);
        setLoading(false);
      })
      .catch((err) => {
        console.error("Event API error:", err);
        setError("Events could not be loaded at this time.");
        setLoading(false);
      });
  }, []);

  // Load only this authenticated member's RSVPs.
  useEffect(() => {
    fetch("http://localhost:8080/api/event-registrations/me", {
      credentials: "include",
    })
      .then((response) => {
        if (!response.ok) throw new Error("Unable to load your registrations.");
        return response.json();
      })
      .then(setRegistrations)
      .catch((err) => setRegistrationError(err.message));
  }, []);

  async function handleEventRegistration(event) {
    setRegistrationMessage("");
    setRegistrationError("");
    setSubmittingEventId(event.eventId);
    try {
      const tokenResponse = await fetch("http://localhost:8080/api/auth/csrf", {
        credentials: "include",
      });
      if (!tokenResponse.ok) throw new Error("Unable to prepare secure registration.");
      const csrf = await tokenResponse.json();
      const response = await fetch("http://localhost:8080/api/event-registrations", {
        method: "POST",
        credentials: "include",
        headers: {
          "Content-Type": "application/json",
          [csrf.headerName]: csrf.token,
        },
        body: JSON.stringify({ eventId: event.eventId }),
      });
      const data = await response.json();
      if (!response.ok) throw new Error(data.message || "Registration failed.");
      setRegistrations((current) => [...current.filter(
        (item) => item.eventId !== event.eventId
      ), data]);
      setRegistrationMessage(`You are registered for ${event.title}.`);
    } catch (err) {
      setRegistrationError(err.message || "Registration failed.");
    } finally {
      setSubmittingEventId(null);
    }
  }

  async function handleCancelRegistration(registration) {
    setRegistrationMessage("");
    setRegistrationError("");
    setSubmittingEventId(registration.eventId);
    try {
      const tokenResponse = await fetch("http://localhost:8080/api/auth/csrf", {
        credentials: "include",
      });
      if (!tokenResponse.ok) throw new Error("Unable to prepare secure cancellation.");
      const csrf = await tokenResponse.json();
      const response = await fetch(
        `http://localhost:8080/api/event-registrations/${registration.registrationId}`,
        { method: "DELETE", credentials: "include",
          headers: { [csrf.headerName]: csrf.token } }
      );
      if (!response.ok) {
        const data = await response.json();
        throw new Error(data.message || "Cancellation failed.");
      }
      setRegistrations((current) => current.map((item) =>
        item.registrationId === registration.registrationId
          ? { ...item, status: "CANCELLED" } : item
      ));
      setRegistrationMessage("Registration cancelled.");
    } catch (err) {
      setRegistrationError(err.message || "Cancellation failed.");
    } finally {
      setSubmittingEventId(null);
    }
  }

  /**
   * Converts the database date into a readable date.
   */
  const formatDate = (dateValue) => {
    return new Date(dateValue).toLocaleDateString("en-US", {
      month: "short",
      day: "numeric",
      year: "numeric",
    });
  };

  /**
   * Converts the database date into a readable time.
   */
  const formatTime = (dateValue) => {
    return new Date(dateValue).toLocaleTimeString("en-US", {
      hour: "numeric",
      minute: "2-digit",
    });
  };

  return (
    <div className="events-page">
      <section className="events-hero">
        <span className="eyebrow">WHAT'S HAPPENING</span>
        <h2>Upcoming Events</h2>
        <p>
          Stay connected with worship services, fellowship opportunities,
          and gatherings at New Life in Christ Ministries.
        </p>
      </section>

      <section className="events-content">
        <div className="events-heading">
          <div>
            <span className="eyebrow">NLIC COMMUNITY</span>
            <h2>Find Your Next Event</h2>
          </div>

          <p>
            Event information below is provided through the NLIC Connect
            event management system.
          </p>
        </div>

        {registrationMessage && <p role="status" className="system-message">{registrationMessage}</p>}
        {registrationError && <p role="alert" className="system-message error-message">{registrationError}</p>}

        {loading && (
          <div className="system-message">
            Loading upcoming events...
          </div>
        )}

        {error && (
          <div className="system-message error-message">
            {error}
          </div>
        )}

        {!loading && !error && events.length === 0 && (
          <div className="system-message">
            No upcoming events are currently available.
          </div>
        )}

        {!loading && !error && (
          <div className="events-grid">
            {events.map((event) => (
              <article className="event-card" key={event.eventId}>
                <div className="event-date">
                  <span>
                    {new Date(event.eventDate)
                      .toLocaleDateString("en-US", { month: "short" })
                      .toUpperCase()}
                  </span>

                  <strong>
                    {new Date(event.eventDate).getDate()}
                  </strong>
                </div>

                <div className="event-card-content">
                  <span className="event-type">
                    NLIC EVENT
                  </span>

                  <h3>{event.title}</h3>

                  <div className="event-meta">
                    <span>{formatDate(event.eventDate)}</span>
                    <span>•</span>
                    <span>{formatTime(event.eventDate)}</span>
                  </div>

                  <p>{event.description}</p>

                  <div className="event-location">
                    <strong>Location</strong>
                    <span>{event.location}</span>
                  </div>

                  <div className="event-footer">
                    <span>
                      Capacity: {event.capacity ?? "Open"}
                    </span>

                    <span
                      className={
                        event.registrationOpen
                          ? "registration-open"
                          : "registration-closed"
                      }
                    >
                      {event.registrationOpen
                        ? "Registration Open"
                        : "Registration Closed"}
                    </span>
                  </div>
                  {(() => {
                    const registration = registrations.find(
                      (item) => item.eventId === event.eventId && item.status === "REGISTERED"
                    );
                    const past = new Date(event.eventDate) <= new Date();
                    const disabled = submittingEventId === event.eventId;
                    return (
                      <button
                        type="button"
                        className="volunteer-button"
                        disabled={disabled || (!registration && (!event.registrationOpen || past))}
                        onClick={() => registration
                          ? handleCancelRegistration(registration)
                          : handleEventRegistration(event)}
                        aria-label={registration
                          ? `Cancel registration for ${event.title}`
                          : `Register for ${event.title}`}
                      >
                        {disabled ? "Processing..." : registration
                          ? "Cancel My RSVP"
                          : !event.registrationOpen || past
                            ? "Registration Closed" : "Register for Event"}
                      </button>
                    );
                  })()}
                </div>
              </article>
            ))}
          </div>
        )}
      </section>
    </div>
  );
}

export default Events;