import { useEffect, useState } from "react";

function Volunteer() {
  const [opportunities, setOpportunities] = useState([]);
  const [signedUpIds, setSignedUpIds] = useState([]);
  const [loading, setLoading] = useState(true);
  const [signingUpId, setSigningUpId] = useState(null);
  const [error, setError] = useState("");
  const [message, setMessage] = useState("");

  // Temporary development user until authentication is completed.
  const currentUserId = 1;

  useEffect(() => {
    loadVolunteerData();
  }, []);

  async function loadVolunteerData() {
    try {
      setLoading(true);
      setError("");

      const [opportunitiesResponse, signupsResponse] = await Promise.all([
        fetch("http://localhost:8080/api/volunteer/opportunities"),
        fetch(
          `http://localhost:8080/api/volunteer/signups/user/${currentUserId}`
        ),
      ]);

      if (!opportunitiesResponse.ok || !signupsResponse.ok) {
        throw new Error("Unable to load volunteer information.");
      }

      const opportunitiesData = await opportunitiesResponse.json();
      const signupsData = await signupsResponse.json();

      setOpportunities(opportunitiesData);

      setSignedUpIds(
        signupsData
          .filter((signup) => signup.status === "SIGNED_UP")
          .map((signup) => signup.opportunityId)
      );
    } catch (err) {
      console.error("Volunteer API error:", err);
      setError("Volunteer opportunities could not be loaded at this time.");
    } finally {
      setLoading(false);
    }
  }

  async function handleSignup(opportunity) {
    try {
      setSigningUpId(opportunity.opportunityId);
      setMessage("");
      setError("");

      const response = await fetch(
        "http://localhost:8080/api/volunteer/signups",
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify({
            userId: currentUserId,
            opportunityId: opportunity.opportunityId,
            notes: `Signed up through NLIC Connect for ${opportunity.title}`,
          }),
        }
      );

      const data = await response.json();

      if (!response.ok) {
        throw new Error(
          data.message || "Volunteer signup could not be completed."
        );
      }

      setSignedUpIds((current) => [
        ...current,
        opportunity.opportunityId,
      ]);

      setMessage(
        `You are now signed up to serve with ${opportunity.title}.`
      );
    } catch (err) {
      console.error("Volunteer signup error:", err);
      setError(err.message);
    } finally {
      setSigningUpId(null);
    }
  }

  const formatDate = (dateString) => {
    if (!dateString) return "Date to be announced";

    return new Date(dateString).toLocaleDateString("en-US", {
      month: "short",
      day: "numeric",
      year: "numeric",
    });
  };

  const formatTime = (dateString) => {
    if (!dateString) return "";

    return new Date(dateString).toLocaleTimeString("en-US", {
      hour: "numeric",
      minute: "2-digit",
    });
  };

  return (
    <main className="volunteer-page">
      <section className="volunteer-hero">
        <div className="volunteer-hero-content">
          <p className="section-label">MAKE A DIFFERENCE</p>
          <h1>Serve With Us</h1>
          <p>
            Use your gifts to serve New Life in Christ Ministries and make an
            impact in our church and community.
          </p>
        </div>
      </section>

      <section className="volunteer-content">
        <div className="volunteer-heading">
          <div>
            <p className="section-label">GET INVOLVED</p>
            <h2>Volunteer Opportunities</h2>
          </div>

          <p>
            Find a place to serve, connect with others, and make a difference.
          </p>
        </div>

        {message && (
          <div className="volunteer-success">
            {message}
          </div>
        )}

        {error && (
          <div className="volunteer-error">
            {error}
          </div>
        )}

        {loading && (
          <div className="volunteer-message">
            Loading volunteer opportunities...
          </div>
        )}

        {!loading && !error && opportunities.length === 0 && (
          <div className="volunteer-message">
            <h3>No opportunities available yet</h3>
            <p>Please check back soon for new ways to serve.</p>
          </div>
        )}

        {!loading && !error && opportunities.length > 0 && (
          <div className="volunteer-grid">
            {opportunities.map((opportunity) => {
              const alreadySignedUp = signedUpIds.includes(
                opportunity.opportunityId
              );

              return (
                <article
                  className="volunteer-card"
                  key={opportunity.opportunityId}
                >
                  <div className="volunteer-number">
                    {String(opportunity.opportunityId).padStart(2, "0")}
                  </div>

                  <p className="section-label">SERVE</p>

                  <h3>{opportunity.title}</h3>

                  <p className="volunteer-description">
                    {opportunity.description}
                  </p>

                  <div className="volunteer-details">
                    <div>
                      <span>DATE</span>
                      <p>{formatDate(opportunity.startDate)}</p>
                    </div>

                    <div>
                      <span>TIME</span>
                      <p>{formatTime(opportunity.startDate)}</p>
                    </div>

                    <div>
                      <span>LOCATION</span>
                      <p>{opportunity.location || "NLIC"}</p>
                    </div>

                    {opportunity.minAge && (
                      <div>
                        <span>MINIMUM AGE</span>
                        <p>{opportunity.minAge}+</p>
                      </div>
                    )}
                  </div>

                  <button
                    className={
                      alreadySignedUp
                        ? "volunteer-button signed-up"
                        : "volunteer-button"
                    }
                    type="button"
                    disabled={
                      alreadySignedUp ||
                      signingUpId === opportunity.opportunityId
                    }
                    onClick={() => handleSignup(opportunity)}
                  >
                    {alreadySignedUp
                      ? "✓ You're Signed Up"
                      : signingUpId === opportunity.opportunityId
                      ? "Signing Up..."
                      : "Sign Up to Serve"}
                  </button>
                </article>
              );
            })}
          </div>
        )}
      </section>
    </main>
  );
}

export default Volunteer;