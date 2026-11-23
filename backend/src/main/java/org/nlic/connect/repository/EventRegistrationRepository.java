package org.nlic.connect.repository;

import org.nlic.connect.model.EventRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

/** Repository for the event_registrations table in the approved design. */
public interface EventRegistrationRepository extends JpaRepository<EventRegistration, Long> {
    Optional<EventRegistration> findByUserIdAndEventId(Long userId, Long eventId);
    Optional<EventRegistration> findByRegistrationIdAndUserId(Long registrationId, Long userId);
    List<EventRegistration> findByUserIdOrderByRegistrationDateDesc(Long userId);
    List<EventRegistration> findByEventIdOrderByRegistrationDateDesc(Long eventId);
    long countByEventIdAndStatus(Long eventId, String status);
    /** Includes active and cancelled RSVPs to preserve history on deletion. */
    long countByEventId(Long eventId);

    /** Counts all currently registered RSVPs, including historical event dates. */
    long countByStatus(String status);
}
