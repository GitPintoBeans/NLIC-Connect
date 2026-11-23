package org.nlic.connect.controller;

import jakarta.validation.Valid;
import org.nlic.connect.dto.EventWriteRequest;
import org.nlic.connect.model.Event;
import org.nlic.connect.service.EventAdminService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/** Only ROLE_ADMIN and ROLE_LEADER can use these endpoints (SecurityConfig). */
@RestController
@RequestMapping("/api/admin/events")
public class EventAdminController {
    private final EventAdminService service;

    public EventAdminController(EventAdminService service) {
        this.service = service;
    }

    @GetMapping
    public List<Event> listEvents() {
        return service.list();
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody EventWriteRequest request,
                                    Authentication authentication) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(service.create(authentication.getName(), request));
        } catch (IllegalStateException exception) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", exception.getMessage()));
        }
    }

    @PutMapping("/{eventId}")
    public ResponseEntity<?> update(@PathVariable Long eventId,
                                    @Valid @RequestBody EventWriteRequest request) {
        try {
            return ResponseEntity.ok(service.update(eventId, request));
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", exception.getMessage()));
        } catch (IllegalStateException exception) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", exception.getMessage()));
        }
    }

    @DeleteMapping("/{eventId}")
    public ResponseEntity<?> delete(@PathVariable Long eventId) {
        try {
            service.delete(eventId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", exception.getMessage()));
        } catch (IllegalStateException exception) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", exception.getMessage()));
        }
    }
}
