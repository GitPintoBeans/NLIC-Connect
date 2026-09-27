package org.nlic.connect.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Represents a prayer request submitted by an NLIC Connect member.
 */
@Entity
@Table(name = "prayer_requests")
public class PrayerRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "prayer_request_id")
    private Long prayerRequestId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(name = "request_text", nullable = false, columnDefinition = "TEXT")
    private String requestText;

    @Column(name = "privacy_level", nullable = false, length = 20)
    private String privacyLevel = "PRIVATE";

    @Column(nullable = false, length = 20)
    private String status = "ACTIVE";

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;

    public PrayerRequest() {
    }

    public Long getPrayerRequestId() {
        return prayerRequestId;
    }

    public void setPrayerRequestId(Long prayerRequestId) {
        this.prayerRequestId = prayerRequestId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRequestText() {
        return requestText;
    }

    public void setRequestText(String requestText) {
        this.requestText = requestText;
    }

    public String getPrivacyLevel() {
        return privacyLevel;
    }

    public void setPrivacyLevel(String privacyLevel) {
        this.privacyLevel = privacyLevel;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}