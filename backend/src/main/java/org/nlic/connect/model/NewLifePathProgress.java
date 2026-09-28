package org.nlic.connect.model;

import jakarta.persistence.*;
import java.time.LocalDate;

/**
 * Represents a user's progress for a single New Life Path step.
 * Each record tracks the user, the step, completion status, and any optional notes.
 */
@Entity
@Table(
    name = "new_life_path_progress",
    uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "step_id"})
)
public class NewLifePathProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "progress_id")
    private Long progressId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "step_id", nullable = false)
    private Long stepId;

    @Column(name = "completed_date")
    private LocalDate completedDate;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(nullable = false, length = 20)
    private String status = "NOT_STARTED";

    public NewLifePathProgress() {}

    public Long getProgressId() {
        return progressId;
    }

    public void setProgressId(Long progressId) {
        this.progressId = progressId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getStepId() {
        return stepId;
    }

    public void setStepId(Long stepId) {
        this.stepId = stepId;
    }

    public LocalDate getCompletedDate() {
        return completedDate;
    }

    public void setCompletedDate(LocalDate completedDate) {
        this.completedDate = completedDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}