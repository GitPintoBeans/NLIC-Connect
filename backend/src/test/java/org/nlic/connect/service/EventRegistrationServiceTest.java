package org.nlic.connect.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.nlic.connect.model.Event;
import org.nlic.connect.model.EventRegistration;
import org.nlic.connect.model.User;
import org.nlic.connect.repository.EventRepository;
import org.nlic.connect.repository.EventRegistrationRepository;
import org.nlic.connect.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/** Iteration 3 component tests: registration business rules without a live database. */
@ExtendWith(MockitoExtension.class)
class EventRegistrationServiceTest {
    @Mock private EventRegistrationRepository registrationRepository;
    @Mock private EventRepository eventRepository;
    @Mock private UserRepository userRepository;
    @InjectMocks private EventRegistrationService service;

    private static final String MEMBER = "member@example.com";
    private static final long EVENT = 10L;
    private static final long USER = 20L;

    private Event futureEvent(Integer capacity, boolean open) {
        Event event = new Event();
        event.setEventId(EVENT);
        event.setCapacity(capacity);
        event.setRegistrationOpen(open);
        event.setEventDate(LocalDateTime.now().plusDays(3));
        return event;
    }

    private void mockMemberAndEvent(Event event) {
        User user = new User();
        user.setUserId(USER);
        when(userRepository.findByEmail(MEMBER)).thenReturn(Optional.of(user));
        when(eventRepository.findByIdForUpdate(EVENT)).thenReturn(Optional.of(event));
    }

    @Test
    void newRsvpPersistsAuthenticatedMembersId() {
        mockMemberAndEvent(futureEvent(5, true));
        when(registrationRepository.findByUserIdAndEventId(USER, EVENT))
                .thenReturn(Optional.empty());
        when(registrationRepository.countByEventIdAndStatus(EVENT, "REGISTERED"))
                .thenReturn(1L);
        when(registrationRepository.save(any(EventRegistration.class)))
                .thenAnswer(call -> call.getArgument(0));

        EventRegistration result = service.registerCurrentMember(MEMBER, EVENT);
        assertEquals(USER, result.getUserId());
        assertEquals(EVENT, result.getEventId());
        assertEquals("REGISTERED", result.getStatus());
        verify(registrationRepository).save(any(EventRegistration.class));
    }

    @Test
    void duplicateActiveRsvpIsRejected() {
        mockMemberAndEvent(futureEvent(5, true));
        EventRegistration existing = new EventRegistration();
        existing.setStatus("REGISTERED");
        when(registrationRepository.findByUserIdAndEventId(USER, EVENT))
                .thenReturn(Optional.of(existing));
        assertThrows(IllegalStateException.class,
                () -> service.registerCurrentMember(MEMBER, EVENT));
        verify(registrationRepository, never()).save(any());
    }

    @Test
    void capacityIsEnforced() {
        mockMemberAndEvent(futureEvent(2, true));
        when(registrationRepository.findByUserIdAndEventId(USER, EVENT))
                .thenReturn(Optional.empty());
        when(registrationRepository.countByEventIdAndStatus(EVENT, "REGISTERED"))
                .thenReturn(2L);
        assertThrows(IllegalStateException.class,
                () -> service.registerCurrentMember(MEMBER, EVENT));
        verify(registrationRepository, never()).save(any());
    }

    @Test
    void closedEventIsRejected() {
        mockMemberAndEvent(futureEvent(5, false));
        assertThrows(IllegalStateException.class,
                () -> service.registerCurrentMember(MEMBER, EVENT));
        verify(registrationRepository, never()).save(any());
    }

    @Test
    void pastEventIsRejected() {
        Event past = futureEvent(5, true);
        past.setEventDate(LocalDateTime.now().minusDays(1));
        mockMemberAndEvent(past);
        assertThrows(IllegalStateException.class,
                () -> service.registerCurrentMember(MEMBER, EVENT));
        verify(registrationRepository, never()).save(any());
    }

    @Test
    void cancelledRsvpCanBeReactivated() {
        mockMemberAndEvent(futureEvent(5, true));
        EventRegistration cancelled = new EventRegistration();
        cancelled.setStatus("CANCELLED");
        cancelled.setRegistrationId(91L);
        when(registrationRepository.findByUserIdAndEventId(USER, EVENT))
                .thenReturn(Optional.of(cancelled));
        when(registrationRepository.countByEventIdAndStatus(EVENT, "REGISTERED"))
                .thenReturn(0L);
        when(registrationRepository.save(any(EventRegistration.class)))
                .thenAnswer(call -> call.getArgument(0));

        EventRegistration result = service.registerCurrentMember(MEMBER, EVENT);
        assertSame(cancelled, result);
        assertEquals("REGISTERED", result.getStatus());
        assertEquals(91L, result.getRegistrationId());
    }

    @Test
    void memberCannotCancelAnotherMembersRsvp() {
        User user = new User();
        user.setUserId(USER);
        when(userRepository.findByEmail(MEMBER)).thenReturn(Optional.of(user));
        when(registrationRepository.findByRegistrationIdAndUserId(99L, USER))
                .thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> service.cancelMine(MEMBER, 99L));
        verify(registrationRepository, never()).save(any());
    }
}
