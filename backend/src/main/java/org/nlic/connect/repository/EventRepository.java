package org.nlic.connect.repository;

import org.nlic.connect.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;

import java.util.List;

/**
 * Provides database access operations for NLIC Connect events.
 *
 * @author James Pinto
 */

public interface EventRepository extends JpaRepository<Event, Long> {

    /**
     * Retrieves all events ordered chronologically.
     *
     * @return list of events ordered by event date
     */
    List<Event> findAllByOrderByEventDateAsc();

    /** Serialize RSVP capacity checks on this event row. */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select e from Event e where e.eventId = :eventId")
    java.util.Optional<Event> findByIdForUpdate(@Param("eventId") Long eventId);

    long countByEventDateAfter(java.time.LocalDateTime now);
    List<Event> findTop5ByEventDateAfterOrderByEventDateAsc(java.time.LocalDateTime now);
}
