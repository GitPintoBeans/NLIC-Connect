import { useEffect, useState } from "react";

/**
 * Displays upcoming church events retrieved from the
 * NLIC Connect Spring Boot REST API.
 */
function Events() {
  const [events, setEvents] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

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