package org.nlic.connect.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.nlic.connect.dto.EventWriteRequest;
import org.nlic.connect.model.Event;
import org.nlic.connect.model.User;
import org.nlic.connect.repository.EventRegistrationRepository;
import org.nlic.connect.repository.EventRepository;
import org.nlic.connect.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/** Iteration 3 admin event-management tests; uses mocked repositories, not real member data. */
@ExtendWith(MockitoExtension.class)
class EventAdminServiceTest {
    @Mock EventRepository events;
    @Mock EventRegistrationRepository registrations;
    @Mock UserRepository users;
    @InjectMocks EventAdminService service;

    private EventWriteRequest request(Integer capacity) {
        return new EventWriteRequest("  Test event  ", "Description",
                LocalDateTime.now().plusDays(12), "Fellowship Hall", capacity, true);
    }

    @Test
    void creatorIsDerivedFromAuthenticatedEmail() {
        User user = new User();
        user.setUserId(42L);
        when(users.findByEmail("leader@example.com")).thenReturn(Optional.of(user));
        when(events.save(any(Event.class))).thenAnswer(call -> call.getArgument(0));

        Event created = service.create("leader@example.com", request(10));
        assertEquals(42L, created.getCreatedBy());
        assertEquals("Test event", created.getTitle());
        verify(events).save(any(Event.class));
    }

    @Test
    void cannotDecreaseCapacityBelowCurrentRsvps() {
        Event existing = new Event();
        existing.setCreatedBy(3L);
        when(events.findByIdForUpdate(4L)).thenReturn(Optional.of(existing));
        when(registrations.countByEventIdAndStatus(4L, "REGISTERED"))
                .thenReturn(3L);
        IllegalStateException error = assertThrows(IllegalStateException.class,
                () -> service.update(4L, request(2)));
        assertTrue(error.getMessage().contains("Capacity"));
        verify(events, never()).save(any(Event.class));
    }

    @Test
    void updatePreservesOriginalCreator() {
        Event existing = new Event();
        existing.setCreatedBy(99L);
        when(events.findByIdForUpdate(4L)).thenReturn(Optional.of(existing));
        when(events.save(any(Event.class))).thenAnswer(call -> call.getArgument(0));
        Event updated = service.update(4L, request(12));
        assertEquals(99L, updated.getCreatedBy());
        assertEquals(12, updated.getCapacity());
    }

    @Test
    void deletionWithRegistrationHistoryIsRejected() {
        when(events.findByIdForUpdate(4L)).thenReturn(Optional.of(new Event()));
        when(registrations.countByEventId(4L)).thenReturn(1L);
        assertThrows(IllegalStateException.class, () -> service.delete(4L));
        verify(events, never()).delete(any(Event.class));
    }

    @Test
    void deletionWithoutRegistrationsIsAllowed() {
        Event existing = new Event();
        when(events.findByIdForUpdate(4L)).thenReturn(Optional.of(existing));
        service.delete(4L);
        verify(events).delete(existing);
    }

    @Test
    void missingEventCannotBeEdited() {
        when(events.findByIdForUpdate(999L)).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class,
                () -> service.update(999L, request(10)));
    }

    @Test
    void existingPastEventCanHaveRegistrationClosed() {
        LocalDateTime historicalDate = LocalDateTime.of(2026, 10, 4, 11, 0);
        Event existing = new Event();
        existing.setEventDate(historicalDate);
        existing.setRegistrationOpen(true);
        existing.setCreatedBy(1L);
        when(events.findByIdForUpdate(1L)).thenReturn(Optional.of(existing));
        when(events.save(any(Event.class))).thenAnswer(call -> call.getArgument(0));

        EventWriteRequest closing = new EventWriteRequest(
                "Sunday Worship Service", "", historicalDate,
                "Main Sanctuary", 150, false);
        Event result = service.update(1L, closing);

        assertFalse(result.getRegistrationOpen());
        assertEquals(historicalDate, result.getEventDate());
        assertEquals(1L, result.getCreatedBy());
        verify(events).save(existing);
    }

    @Test
    void existingPastEventCannotBeRescheduledToDifferentPastDate() {
        Event existing = new Event();
        existing.setEventDate(LocalDateTime.of(2026, 10, 4, 11, 0));
        when(events.findByIdForUpdate(1L)).thenReturn(Optional.of(existing));

        EventWriteRequest invalid = new EventWriteRequest(
                "Sunday Worship Service", "", LocalDateTime.of(2026, 10, 5, 11, 0),
                "Main Sanctuary", 150, false);
        assertThrows(IllegalStateException.class, () -> service.update(1L, invalid));
        verify(events, never()).save(any(Event.class));
    }

    @Test
    void pastEventCannotHaveRegistrationReopened() {
        LocalDateTime historicalDate = LocalDateTime.of(2026, 10, 4, 11, 0);
        Event existing = new Event();
        existing.setEventDate(historicalDate);
        when(events.findByIdForUpdate(1L)).thenReturn(Optional.of(existing));

        EventWriteRequest invalid = new EventWriteRequest(
                "Sunday Worship Service", "", historicalDate,
                "Main Sanctuary", 150, true);
        assertThrows(IllegalStateException.class, () -> service.update(1L, invalid));
        verify(events, never()).save(any(Event.class));
    }

    @Test
    void pastEventDateIsRejectedBeforePersistence() {
        EventWriteRequest bad = new EventWriteRequest("Test", "", LocalDateTime.now().minusDays(1),
                "Church", 10, true);
        assertThrows(IllegalStateException.class,
                () -> service.create("leader@example.com", bad));
        verify(events, never()).save(any(Event.class));
    }
}
