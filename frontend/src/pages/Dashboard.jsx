import { Link } from "react-router-dom";

/**
 * Displays the primary member dashboard for NLIC Connect.
 */
function Dashboard() {
  return (
    <>
      <section className="hero">
        <div>
          <span className="eyebrow">MEMBER DASHBOARD</span>
          <h2>Welcome to NLIC Connect</h2>
          <p>
            Stay connected with New Life in Christ Ministries through
            upcoming events, prayer, service opportunities, and your
            New Life Path.
          </p>

          <div className="hero-actions">
            <Link className="primary-button" to="/events">
              View Upcoming Events
            </Link>

            <Link className="secondary-button" to="/new-life-path">
              View New Life Path
            </Link>
          </div>
        </div>

        <div className="verse-card">
          <span>COMMUNITY</span>
          <blockquote>
            “For where two or three gather in my name, there am I with them.”
          </blockquote>
          <p>Matthew 18:20</p>
        </div>
      </section>

      <section className="dashboard-section">
        <div className="section-heading">
          <div>
            <span className="eyebrow">GET CONNECTED</span>
            <h2>Your NLIC Community</h2>
          </div>

          <p>
            Access the tools designed to help members connect, grow,
            and serve.
          </p>
        </div>

        <div className="feature-grid">
          <Link className="feature-card" to="/events">
            <div className="feature-icon">01</div>
            <h3>Events</h3>
            <p>
              Discover upcoming services, fellowship opportunities,
              and church events.
            </p>
            <span>Explore Events →</span>
          </Link>

          <Link className="feature-card" to="/prayer">
            <div className="feature-icon">02</div>
            <h3>Prayer Requests</h3>
            <p>
              A future module for submitting and managing member
              prayer requests.
            </p>
            <span>View Module →</span>
          </Link>

          <Link className="feature-card" to="/volunteer">
            <div className="feature-icon">03</div>
            <h3>Volunteer</h3>
            <p>
              A future module connecting members with opportunities
              to serve.
            </p>
            <span>View Module →</span>
          </Link>

          <Link className="feature-card" to="/new-life-path">
            <div className="feature-icon">04</div>
            <h3>New Life Path</h3>
            <p>
              Follow the discipleship journey designed to encourage
              spiritual growth.
            </p>
            <span>View Path →</span>
          </Link>
        </div>
      </section>
    </>
  );
}

export default Dashboard;