package org.nlic.connect.service;

import org.nlic.connect.dto.EventWriteRequest;
import org.nlic.connect.model.Event;
import org.nlic.connect.model.User;
import org.nlic.connect.repository.EventRegistrationRepository;
import org.nlic.connect.repository.EventRepository;
import org.nlic.connect.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/** Server-side administrative business rules; endpoints are authorized in SecurityConfig. */
@Service
public class EventAdminService {
    private final EventRepository events;
    private final EventRegistrationRepository registrations;
    private final UserRepository users;

    public EventAdminService(EventRepository events,
                             EventRegistrationRepository registrations,
                             UserRepository users) {
        this.events = events;
        this.registrations = registrations;
        this.users = users;
    }

    @Transactional(readOnly = true)
    public List<Event> list() {
        return events.findAllByOrderByEventDateAsc();
    }

    @Transactional
    public Event create(String authenticatedEmail, EventWriteRequest request) {
        verifyFuture(request);
        User creator = users.findByEmail(authenticatedEmail)
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found."));
        Event event = new Event();
        event.setCreatedBy(creator.getUserId());
        copyEditableFields(event, request);
        return events.save(event);
    }

    @Transactional
    public Event update(Long eventId, EventWriteRequest request) {
        verifyFields(request);
        // Use the same event-row lock as RSVP registration to serialize capacity changes.
        Event event = events.findByIdForUpdate(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found."));
        verifyUpdateDate(event, request);
        long confirmed = registrations.countByEventIdAndStatus(eventId, "REGISTERED");
        if (request.capacity() != null && request.capacity() < confirmed) {
            throw new IllegalStateException("Capacity cannot be lower than active registrations.");
        }
        copyEditableFields(event, request);
        return events.save(event);
    }

    @Transactional
    public void delete(Long eventId) {
        Event event = events.findByIdForUpdate(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event not found."));
        // Do not delete events with historic or active registrations: keep the audit trail.
        if (registrations.countByEventId(eventId) > 0) {
            throw new IllegalStateException(
                    "Event has registration history. Close registration instead of deleting it.");
        }
        events.delete(event);
    }

    /** New events may not be created in the past. */
    private void verifyFuture(EventWriteRequest request) {
        verifyFields(request);
        if (!request.eventDate().isAfter(LocalDateTime.now())) {
            throw new IllegalStateException("Event date must be in the future.");
        }
    }

    /** Shared field validation for both new and existing events. */
    private void verifyFields(EventWriteRequest request) {
        if (request.eventDate() == null) {
            throw new IllegalStateException("Event date is required.");
        }
        if (request.title() == null || request.title().isBlank()) {
            throw new IllegalStateException("Event title is required.");
        }
        if (request.capacity() != null && request.capacity() < 1) {
            throw new IllegalStateException("Capacity must be at least 1 or left blank.");
        }
        if (request.registrationOpen() == null) {
            throw new IllegalStateException("Registration status is required.");
        }
    }

    /**
     * Allow administrators to close or correct details on existing past events
     * without moving their historical date. Forbid scheduling an event into the
     * past or reopening registrations after its date has passed.
     */
    private void verifyUpdateDate(Event existing, EventWriteRequest request) {
        LocalDateTime requestedDate = request.eventDate();
        if (!requestedDate.isAfter(LocalDateTime.now())) {
            if (existing.getEventDate() == null ||
                    !requestedDate.equals(existing.getEventDate())) {
                throw new IllegalStateException(
                        "An event cannot be rescheduled to a date in the past.");
            }
            if (Boolean.TRUE.equals(request.registrationOpen())) {
                throw new IllegalStateException(
                        "Registration must be closed for past events.");
            }
        }
    }

    private void copyEditableFields(Event event, EventWriteRequest request) {
        event.setTitle(request.title().trim());
        event.setDescription(request.description());
        event.setEventDate(request.eventDate());
        event.setLocation(request.location());
        event.setCapacity(request.capacity());
        event.setRegistrationOpen(request.registrationOpen());
    }
}
