package org.nlic.connect.service;

import java.time.LocalDateTime;
import java.util.List;

import org.nlic.connect.dto.AdminDashboardSummary;
import org.nlic.connect.model.Event;
import org.nlic.connect.repository.EventRegistrationRepository;
import org.nlic.connect.repository.EventRepository;
import org.nlic.connect.repository.NewLifePathProgressRepository;
import org.nlic.connect.repository.PrayerRequestRepository;
import org.nlic.connect.repository.UserRepository;
import org.nlic.connect.repository.VolunteerOpportunityRepository;
import org.nlic.connect.repository.VolunteerSignupRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Provides non-sensitive, live database totals for the administrator dashboard. */
@Service
public class AdminDashboardService {
    private final UserRepository users;
    private final EventRepository events;
    private final EventRegistrationRepository eventRegistrations;
    private final VolunteerOpportunityRepository opportunities;
    private final VolunteerSignupRepository volunteerSignups;
    private final NewLifePathProgressRepository pathProgress;
    private final PrayerRequestRepository prayerRequests;

    public AdminDashboardService(
            UserRepository users,
            EventRepository events,
            EventRegistrationRepository eventRegistrations,
            VolunteerOpportunityRepository opportunities,
            VolunteerSignupRepository volunteerSignups,
            NewLifePathProgressRepository pathProgress,
            PrayerRequestRepository prayerRequests) {
        this.users = users;
        this.events = events;
        this.eventRegistrations = eventRegistrations;
        this.opportunities = opportunities;
        this.volunteerSignups = volunteerSignups;
        this.pathProgress = pathProgress;
        this.prayerRequests = prayerRequests;
    }

    /**
     * Queries aggregate data without loading sensitive member records into the response.
     * Upcoming items are compared against a single time captured per request.
     */
    @Transactional(readOnly = true)
    public AdminDashboardSummary getSummary() {
        LocalDateTime now = LocalDateTime.now();
        List<AdminDashboardSummary.EventPreview> nextEvents = events
                .findTop5ByEventDateAfterOrderByEventDateAsc(now)
                .stream()
                .map(this::toPreview)
                .toList();

        return new AdminDashboardSummary(
                users.countByActiveTrue(),
                events.countByEventDateAfter(now),
                opportunities.countByActiveTrueAndStartDateAfter(now),
                eventRegistrations.countByStatus("REGISTERED"),
                volunteerSignups.countByStatus("SIGNED_UP"),
                pathProgress.countDistinctActiveUsersByStatus("COMPLETED"),
                prayerRequests.count(),
                prayerRequests.countByStatus("ACTIVE"),
                nextEvents);
    }

    private AdminDashboardSummary.EventPreview toPreview(Event event) {
        return new AdminDashboardSummary.EventPreview(
                event.getEventId(),
                event.getTitle(),
                event.getEventDate(),
                event.getLocation(),
                Boolean.TRUE.equals(event.getRegistrationOpen()));
    }
}
