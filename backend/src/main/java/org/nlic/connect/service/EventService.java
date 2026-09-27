package org.nlic.connect.service;

import org.nlic.connect.model.Event;
import org.nlic.connect.repository.EventRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Contains business logic for managing NLIC Connect events.
 *
 * @author James Pinto
 */
@Service
public class EventService {

    private final EventRepository eventRepository;

    /**
     * Creates the EventService with the required repository dependency.
     *
     * @param eventRepository repository used to access event records
     */
    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    /**
     * Retrieves all events in chronological order.
     *
     * @return all available events
     */
    public List<Event> getAllEvents() {
        return eventRepository.findAllByOrderByEventDateAsc();
    }

    /**
     * Retrieves an individual event by its database identifier.
     *
     * @param eventId unique event identifier
     * @return matching event when found
     */
    public Optional<Event> getEventById(Long eventId) {
        return eventRepository.findById(eventId);
    }

    /**
     * Saves a new or modified event.
     *
     * @param event event information to persist
     * @return persisted event
     */
    public Event saveEvent(Event event) {
        return eventRepository.save(event);
    }
}