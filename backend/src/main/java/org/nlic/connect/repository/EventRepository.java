package org.nlic.connect.repository;

import org.nlic.connect.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Provides database access operations for NLIC Connect events.
 *
 * @author James Pinto
 */
@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    /**
     * Retrieves all events ordered chronologically.
     *
     * @return list of events ordered by event date
     */
    List<Event> findAllByOrderByEventDateAsc();
}