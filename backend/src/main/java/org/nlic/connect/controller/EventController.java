package org.nlic.connect.controller;

import jakarta.validation.Valid;
import org.nlic.connect.model.Event;
import org.nlic.connect.service.EventService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Provides REST endpoints for viewing and managing NLIC Connect events.
 *
 * @author James Pinto
 */
@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventService eventService;

    /**
     * Creates the controller with the required event service.
     *
     * @param eventService service containing event business logic
     */
    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    /**
     * Returns all church events in chronological order.
     *
     * @return list of available events
     */
    @GetMapping
    public List<Event> getAllEvents() {
        return eventService.getAllEvents();
    }

    /**
     * Returns a specific event by its identifier.
     *
     * @param id event identifier
     * @return matching event or HTTP 404 when not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Event> getEventById(@PathVariable Long id) {
        return eventService.getEventById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Creates a new church event.
     *
     * @param event event information supplied by the client
     * @return newly persisted event
     */
    @PostMapping
    public Event createEvent(@Valid @RequestBody Event event) {
        return eventService.saveEvent(event);
    }
}