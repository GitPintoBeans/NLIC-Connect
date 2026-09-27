import { useEffect, useState } from "react";

function PrayerRequests() {
  const [requests, setRequests] = useState([]);
  const [formData, setFormData] = useState({
    title: "",
    requestText: "",
    privacyLevel: "PRIVATE",
  });

  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [message, setMessage] = useState("");
  const [error, setError] = useState("");

  const loadPrayerRequests = () => {
    fetch("http://localhost:8080/api/prayer-requests")
      .then((response) => {
        if (!response.ok) {
          throw new Error("Unable to retrieve prayer requests.");
        }
        return response.json();
      })
      .then((data) => {
        setRequests(data);
        setLoading(false);
      })
      .catch((err) => {
        console.error(err);
        setError("Prayer requests could not be loaded.");
        setLoading(false);
      });
  };

  useEffect(() => {
    loadPrayerRequests();
  }, []);

  const handleChange = (event) => {
    const { name, value } = event.target;

    setFormData((previous) => ({
      ...previous,
      [name]: value,
    }));
  };

  const handleSubmit = async (event) => {
    event.preventDefault();

    setSubmitting(true);
    setMessage("");
    setError("");

    const requestBody = {
      userId: 1,
      title: formData.title,
      requestText: formData.requestText,
      privacyLevel: formData.privacyLevel,
    };

    try {
      const response = await fetch(
        "http://localhost:8080/api/prayer-requests",
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify(requestBody),
        }
      );

      if (!response.ok) {
        throw new Error("Prayer request submission failed.");
      }

      setFormData({
        title: "",
        requestText: "",
        privacyLevel: "PRIVATE",
      });

      setMessage(
        "Your prayer request has been submitted successfully."
      );

      loadPrayerRequests();
    } catch (err) {
      console.error(err);
      setError(
        "Your prayer request could not be submitted. Please try again."
      );
    } finally {
      setSubmitting(false);
    }
  };

  const formatDate = (dateValue) => {
    if (!dateValue) return "";

    return new Date(dateValue).toLocaleDateString("en-US", {
      month: "short",
      day: "numeric",
      year: "numeric",
    });
  };

  return (
    <div className="prayer-page">
      <section className="prayer-hero">
        <span className="eyebrow">CONNECT THROUGH PRAYER</span>

        <h2>Prayer Requests</h2>

        <p>
          Share what is on your heart with the New Life in Christ
          Ministries community. Every request matters, whether you
          choose to share it with the church or keep it private.
        </p>
      </section>

      <section className="prayer-content">
        <div className="prayer-form-section">
          <span className="eyebrow">WE ARE HERE FOR YOU</span>
          <h2>How Can We Pray for You?</h2>

          <p className="section-description">
            Submit a prayer request below. You can choose whether the
            request is private or visible to the church community.
          </p>

          <form className="prayer-form" onSubmit={handleSubmit}>
            <div className="form-group">
              <label htmlFor="title">Prayer Request Title</label>

              <input
                id="title"
                name="title"
                type="text"
                maxLength="100"
                value={formData.title}
                onChange={handleChange}
                placeholder="Example: Prayer for my family"
                required
              />
            </div>

            <div className="form-group">
              <label htmlFor="requestText">
                How can we pray for you?
              </label>

              <textarea
                id="requestText"
                name="requestText"
                rows="7"
                value={formData.requestText}
                onChange={handleChange}
                placeholder="Share your prayer request..."
                required
              />
            </div>

            <div className="form-group">
              <label htmlFor="privacyLevel">Privacy</label>

              <select
                id="privacyLevel"
                name="privacyLevel"
                value={formData.privacyLevel}
                onChange={handleChange}
              >
                <option value="PRIVATE">
                  Private - Church leadership only
                </option>

                <option value="PUBLIC">
                  Public - NLIC community
                </option>
              </select>

              <small>
                Private requests are intended for authorized church
                leadership and are not displayed publicly.
              </small>
            </div>

            {message && (
              <div className="prayer-success">{message}</div>
            )}

            {error && (
              <div className="prayer-error">{error}</div>
            )}

            <button
              className="prayer-submit"
              type="submit"
              disabled={submitting}
            >
              {submitting
                ? "Submitting..."
                : "Submit Prayer Request"}
            </button>
          </form>
        </div>

        <div className="prayer-list-section">
          <span className="eyebrow">PRAY WITH US</span>
          <h2>Community Prayer Wall</h2>

          <p className="section-description">
            Public prayer requests shared by members of the NLIC
            community appear here.
          </p>

          {loading && (
            <div className="prayer-empty">
              Loading prayer requests...
            </div>
          )}

          {!loading &&
            requests.filter(
              (request) => request.privacyLevel === "PUBLIC"
            ).length === 0 && (
              <div className="prayer-empty">
                <div className="prayer-symbol">+</div>

                <h3>No public requests yet</h3>

                <p>
                  When community prayer requests are submitted, they
                  will appear here.
                </p>
              </div>
            )}

          <div className="prayer-list">
            {requests
              .filter(
                (request) => request.privacyLevel === "PUBLIC"
              )
              .map((request) => (
                <article
                  className="prayer-card"
                  key={request.prayerRequestId}
                >
                  <span className="prayer-label">
                    PRAYER REQUEST
                  </span>

                  <h3>{request.title}</h3>

                  <p>{request.requestText}</p>

                  <div className="prayer-card-footer">
                    <span>NLIC Member</span>
                    <span>{formatDate(request.createdAt)}</span>
                  </div>
                </article>
              ))}
          </div>
        </div>
      </section>
    </div>
  );
}

export default PrayerRequests;