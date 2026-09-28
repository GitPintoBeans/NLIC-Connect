package org.nlic.connect.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Represents a volunteer's signup for a specific opportunity.
 * Tracks the user, opportunity, status, and any optional notes.
 */
@Entity
@Table(name = "volunteer_signups")
public class VolunteerSignup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "signup_id")
    private Long signupId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "opportunity_id", nullable = false)
    private Long opportunityId;

    @Column(name = "signup_date", nullable = false)
    private LocalDateTime signupDate;

    @Column(nullable = false, length = 20)
    private String status;

    @Column(columnDefinition = "TEXT")
    private String notes;

    public VolunteerSignup() {
    }

    @PrePersist
    public void prePersist() {
        if (signupDate == null) {
            signupDate = LocalDateTime.now();
        }

        if (status == null || status.isBlank()) {
            status = "SIGNED_UP";
        }
    }

    public Long getSignupId() {
        return signupId;
    }

    public void setSignupId(Long signupId) {
        this.signupId = signupId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getOpportunityId() {
        return opportunityId;
    }

    public void setOpportunityId(Long opportunityId) {
        this.opportunityId = opportunityId;
    }

    public LocalDateTime getSignupDate() {
        return signupDate;
    }

    public void setSignupDate(LocalDateTime signupDate) {
        this.signupDate = signupDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}