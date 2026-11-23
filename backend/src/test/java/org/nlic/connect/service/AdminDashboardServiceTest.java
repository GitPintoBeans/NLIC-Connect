package org.nlic.connect.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.nlic.connect.dto.AdminDashboardSummary;
import org.nlic.connect.model.Event;
import org.nlic.connect.repository.EventRegistrationRepository;
import org.nlic.connect.repository.EventRepository;
import org.nlic.connect.repository.NewLifePathProgressRepository;
import org.nlic.connect.repository.PrayerRequestRepository;
import org.nlic.connect.repository.UserRepository;
import org.nlic.connect.repository.VolunteerOpportunityRepository;
import org.nlic.connect.repository.VolunteerSignupRepository;

/** Unit tests of the read-only dashboard report (no live MySQL modifications). */
@ExtendWith(MockitoExtension.class)
class AdminDashboardServiceTest {
    @Mock UserRepository users;
    @Mock EventRepository events;
    @Mock EventRegistrationRepository eventRegistrations;
    @Mock VolunteerOpportunityRepository opportunities;
    @Mock VolunteerSignupRepository volunteerSignups;
    @Mock NewLifePathProgressRepository pathProgress;
    @Mock PrayerRequestRepository prayerRequests;
    @InjectMocks AdminDashboardService service;

    @Test
    void reportsAggregateCountsFromRepositories() {
        when(users.countByActiveTrue()).thenReturn(12L);
        when(events.countByEventDateAfter(any(LocalDateTime.class))).thenReturn(3L);
        when(opportunities.countByActiveTrueAndStartDateAfter(any(LocalDateTime.class)))
                .thenReturn(4L);
        when(eventRegistrations.countByStatus("REGISTERED")).thenReturn(9L);
        when(volunteerSignups.countByStatus("SIGNED_UP")).thenReturn(7L);
        when(pathProgress.countDistinctActiveUsersByStatus("COMPLETED")).thenReturn(5L);
        when(prayerRequests.count()).thenReturn(11L);
        when(prayerRequests.countByStatus("ACTIVE")).thenReturn(8L);

        AdminDashboardSummary result = service.getSummary();

        assertAll(
                () -> assertEquals(12L, result.activeAccounts()),
                () -> assertEquals(3L, result.upcomingEvents()),
                () -> assertEquals(4L, result.upcomingVolunteerOpportunities()),
                () -> assertEquals(9L, result.registeredEventRsvps()),
                () -> assertEquals(7L, result.activeVolunteerSignups()),
                () -> assertEquals(5L, result.newLifePathParticipants()),
                () -> assertEquals(11L, result.prayerRequestsTotal()),
                () -> assertEquals(8L, result.activePrayerRequests()),
                () -> assertTrue(result.nextEvents().isEmpty()));
    }

    @Test
    void previewsOnlyApprovedEventFields() {
        Event event = new Event();
        event.setEventId(4L);
        event.setTitle("Community Gathering");
        event.setEventDate(LocalDateTime.of(2026, 12, 12, 14, 0));
        event.setLocation("Fellowship Hall");
        event.setRegistrationOpen(true);
        when(events.findTop5ByEventDateAfterOrderByEventDateAsc(any(LocalDateTime.class)))
                .thenReturn(List.of(event));

        AdminDashboardSummary result = service.getSummary();
        assertEquals(1, result.nextEvents().size());
        var preview = result.nextEvents().get(0);
        assertEquals(4L, preview.eventId());
        assertEquals("Community Gathering", preview.title());
        assertEquals("Fellowship Hall", preview.location());
        assertTrue(preview.registrationOpen());
    }

    @Test
    void usesSameTimeForUpcomingCountsAndEventPreview() {
        service.getSummary();
        ArgumentCaptor<LocalDateTime> eventTime = ArgumentCaptor.forClass(LocalDateTime.class);
        ArgumentCaptor<LocalDateTime> opportunityTime = ArgumentCaptor.forClass(LocalDateTime.class);
        ArgumentCaptor<LocalDateTime> previewTime = ArgumentCaptor.forClass(LocalDateTime.class);

        verify(events).countByEventDateAfter(eventTime.capture());
        verify(opportunities).countByActiveTrueAndStartDateAfter(opportunityTime.capture());
        verify(events).findTop5ByEventDateAfterOrderByEventDateAsc(previewTime.capture());
        assertEquals(eventTime.getValue(), opportunityTime.getValue());
        assertEquals(eventTime.getValue(), previewTime.getValue());
    }
}
