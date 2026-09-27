import { useEffect, useState } from "react";

function NewLifePath() {
  const [steps, setSteps] = useState([]);
  const [progress, setProgress] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [updatingStep, setUpdatingStep] = useState(null);

  // Temporary until authentication is connected.
  const currentUserId = 1;

  useEffect(() => {
    loadNewLifePath();
  }, []);

  async function loadNewLifePath() {
    try {
      setLoading(true);
      setError("");

      const [stepsResponse, progressResponse] = await Promise.all([
        fetch("http://localhost:8080/api/new-life-path/steps"),
        fetch(
          `http://localhost:8080/api/new-life-path/progress/${currentUserId}`
        ),
      ]);

      if (!stepsResponse.ok || !progressResponse.ok) {
        throw new Error("Unable to load New Life Path.");
      }

      const stepsData = await stepsResponse.json();
      const progressData = await progressResponse.json();

      setSteps(stepsData);
      setProgress(progressData);
    } catch (err) {
      console.error(err);
      setError("New Life Path could not be loaded at this time.");
    } finally {
      setLoading(false);
    }
  }

  function isCompleted(stepId) {
    return progress.some(
      (item) =>
        item.stepId === stepId &&
        item.status === "COMPLETED"
    );
  }

  async function completeStep(stepId) {
    try {
      setUpdatingStep(stepId);
      setError("");

      const response = await fetch(
        `http://localhost:8080/api/new-life-path/progress/${currentUserId}/${stepId}/complete`,
        {
          method: "POST",
        }
      );

      if (!response.ok) {
        throw new Error("Unable to update progress.");
      }

      const savedProgress = await response.json();

      setProgress((currentProgress) => {
        const existingIndex = currentProgress.findIndex(
          (item) => item.stepId === stepId
        );

        if (existingIndex >= 0) {
          return currentProgress.map((item) =>
            item.stepId === stepId ? savedProgress : item
          );
        }

        return [...currentProgress, savedProgress];
      });
    } catch (err) {
      console.error(err);
      setError("Your progress could not be updated.");
    } finally {
      setUpdatingStep(null);
    }
  }

  const completedCount = steps.filter((step) =>
    isCompleted(step.stepId)
  ).length;

  const progressPercent =
    steps.length > 0
      ? Math.round((completedCount / steps.length) * 100)
      : 0;

  return (
    <main className="new-life-path-page">
      <section className="path-hero">
        <div className="path-hero-content">
          <p className="section-label">GROW IN YOUR FAITH</p>
          <h1>The New Life Path</h1>
          <p>
            A step-by-step journey designed to help you grow spiritually,
            build community, discover your God-given purpose, and make a
            difference.
          </p>
        </div>
      </section>

      <section className="path-content">
        <div className="path-intro">
          <div>
            <p className="section-label">YOUR JOURNEY</p>
            <h2>Continue Your New Life Path</h2>
          </div>

          <div className="path-progress-summary">
            <strong>{progressPercent}% Complete</strong>
            <span>
              {completedCount} of {steps.length} steps completed
            </span>
          </div>
        </div>

        <div className="path-progress-bar">
          <div
            className="path-progress-fill"
            style={{ width: `${progressPercent}%` }}
          />
        </div>

        {error && <div className="path-error">{error}</div>}

        {loading ? (
          <div className="path-loading">
            Loading your New Life Path...
          </div>
        ) : (
          <div className="path-steps">
            {steps.map((step) => {
              const completed = isCompleted(step.stepId);

              return (
                <article
                  className={`path-step-card ${
                    completed ? "completed" : ""
                  }`}
                  key={step.stepId}
                >
                  <div className="path-step-number">
                    {completed ? "✓" : step.stepOrder}
                  </div>

                  <div className="path-step-body">
                    <p className="path-category">
                      {step.category}
                    </p>

                    <h3>{step.title}</h3>

                    <p>{step.description}</p>

                    <button
                      type="button"
                      className={
                        completed
                          ? "path-completed-button"
                          : "path-action-button"
                      }
                      disabled={
                        completed || updatingStep === step.stepId
                      }
                      onClick={() => completeStep(step.stepId)}
                    >
                      {completed
                        ? "Step Completed"
                        : updatingStep === step.stepId
                        ? "Saving..."
                        : "Mark as Complete"}
                    </button>
                  </div>
                </article>
              );
            })}
          </div>
        )}
      </section>
    </main>
  );
}

export default NewLifePath;