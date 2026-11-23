package org.nlic.connect.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/** One member's registration for one church event. */
@Entity
@Table(name = "event_registrations", uniqueConstraints =
        @UniqueConstraint(name = "uq_event_registration", columnNames = {"user_id", "event_id"}))
public class EventRegistration {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "registration_id")
    private Long registrationId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "event_id", nullable = false)
    private Long eventId;

    @Column(name = "registration_date", nullable = false)
    private LocalDateTime registrationDate;

    @Column(name = "status", nullable = false, length = 20)
    private String status = "REGISTERED";

    @PrePersist
    void beforeInsert() {
        if (registrationDate == null) registrationDate = LocalDateTime.now();
        if (status == null || status.isBlank()) status = "REGISTERED";
    }

    public Long getRegistrationId() { return registrationId; }
    public void setRegistrationId(Long registrationId) { this.registrationId = registrationId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getEventId() { return eventId; }
    public void setEventId(Long eventId) { this.eventId = eventId; }
    public LocalDateTime getRegistrationDate() { return registrationDate; }
    public void setRegistrationDate(LocalDateTime registrationDate) { this.registrationDate = registrationDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
