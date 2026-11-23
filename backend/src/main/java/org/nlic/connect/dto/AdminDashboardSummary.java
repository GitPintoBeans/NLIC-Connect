package org.nlic.connect.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Aggregate, read-only reporting data for NLIC Connect administrators.
 * No credentials, prayer request content, or individual progress notes are exposed.
 */
public record AdminDashboardSummary(
        long activeAccounts,
        long upcomingEvents,
        long upcomingVolunteerOpportunities,
        long registeredEventRsvps,
        long activeVolunteerSignups,
        long newLifePathParticipants,
        long prayerRequestsTotal,
        long activePrayerRequests,
        List<EventPreview> nextEvents) {

    /** Minimal non-sensitive event details used by the dashboard. */
    public record EventPreview(
            Long eventId,
            String title,
            LocalDateTime eventDate,
            String location,
            boolean registrationOpen) {
    }
}
