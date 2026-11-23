package org.nlic.connect.service;

import org.nlic.connect.model.Event;
import org.nlic.connect.model.EventRegistration;
import org.nlic.connect.model.User;
import org.nlic.connect.repository.EventRegistrationRepository;
import org.nlic.connect.repository.EventRepository;
import org.nlic.connect.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

/** Enforces ownership, registration window, capacity, and duplicate rules. */
@Service
public class EventRegistrationService {
    private final EventRegistrationRepository registrationRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    public EventRegistrationService(EventRegistrationRepository registrationRepository,
                                    EventRepository eventRepository,
                                    UserRepository userRepository) {
        this.registrationRepository = registrationRepository;
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
    }

    private Long currentUserId(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Authenticated member was not found."));
        return user.getUserId();
    }

    @Transactional(readOnly = true)
    public List<EventRegistration> getMine(String authenticatedEmail) {
        return registrationRepository.findByUserIdOrderByRegistrationDateDesc(
                currentUserId(authenticatedEmail));
    }

    @Transactional(readOnly = true)
    public List<EventRegistration> getEventRegistrations(Long eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new IllegalArgumentException("Event was not found.");
        }
        return registrationRepository.findByEventIdOrderByRegistrationDateDesc(eventId);
    }

    /**
     * Uses a locked event row so concurrent requests cannot independently
     * register for the same final seat. Requires transactional MySQL/InnoDB.
     */
    @Transactional
    public EventRegistration registerCurrentMember(String authenticatedEmail, Long eventId) {
        Long userId = currentUserId(authenticatedEmail);
        Event event = eventRepository.findByIdForUpdate(eventId)
                .orElseThrow(() -> new IllegalArgumentException("Event was not found."));

        if (!Boolean.TRUE.equals(event.getRegistrationOpen())) {
            throw new IllegalStateException("Registration is closed for this event.");
        }
        if (event.getEventDate() == null || !event.getEventDate().isAfter(LocalDateTime.now())) {
            throw new IllegalStateException("Registration is closed for past events.");
        }

        EventRegistration existing = registrationRepository
                .findByUserIdAndEventId(userId, eventId).orElse(null);
        if (existing != null && !"CANCELLED".equals(existing.getStatus())) {
            throw new IllegalStateException("You are already registered for this event.");
        }

        Integer capacity = event.getCapacity();
        if (capacity != null && registrationRepository.countByEventIdAndStatus(
                eventId, "REGISTERED") >= capacity) {
            throw new IllegalStateException("This event has reached capacity.");
        }

        EventRegistration registration = existing == null ? new EventRegistration() : existing;
        registration.setEventId(eventId);
        registration.setUserId(userId);
        registration.setStatus("REGISTERED");
        registration.setRegistrationDate(LocalDateTime.now());
        return registrationRepository.save(registration);
    }

    /** Cancellation is restricted to the authenticated owner's record. */
    @Transactional
    public void cancelMine(String authenticatedEmail, Long registrationId) {
        Long userId = currentUserId(authenticatedEmail);
        EventRegistration registration = registrationRepository
                .findByRegistrationIdAndUserId(registrationId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Registration was not found."));
        if (!"REGISTERED".equals(registration.getStatus())) {
            throw new IllegalStateException("Only active registrations can be cancelled.");
        }
        registration.setStatus("CANCELLED");
        registrationRepository.save(registration);
    }
}
