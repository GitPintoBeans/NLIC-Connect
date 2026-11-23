package org.nlic.connect.controller;

import jakarta.validation.Valid;
import org.nlic.connect.dto.EventRegistrationRequest;
import org.nlic.connect.dto.EventRegistrationResponse;
import org.nlic.connect.model.EventRegistration;
import org.nlic.connect.service.EventRegistrationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

/** Secured REST API for member event RSVPs and leader review. */
@RestController
@RequestMapping("/api/event-registrations")
public class EventRegistrationController {
    private final EventRegistrationService service;

    public EventRegistrationController(EventRegistrationService service) {
        this.service = service;
    }

    @GetMapping("/me")
    public List<EventRegistrationResponse> getMine(Authentication authentication) {
        return service.getMine(authentication.getName()).stream()
                .map(EventRegistrationResponse::from).toList();
    }

    @PostMapping
    public ResponseEntity<?> register(@Valid @RequestBody EventRegistrationRequest request,
                                      Authentication authentication) {
        try {
            EventRegistration created = service.registerCurrentMember(
                    authentication.getName(), request.eventId());
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(EventRegistrationResponse.from(created));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", ex.getMessage()));
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", ex.getMessage()));
        }
    }

    @DeleteMapping("/{registrationId}")
    public ResponseEntity<?> cancelMine(@PathVariable Long registrationId,
                                        Authentication authentication) {
        try {
            service.cancelMine(authentication.getName(), registrationId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", ex.getMessage()));
        } catch (IllegalStateException ex) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", ex.getMessage()));
        }
    }

    /** Only ADMIN or LEADER can retrieve another member's registrations. */
    @GetMapping("/event/{eventId}")
    public ResponseEntity<?> getRegistrationsForEvent(@PathVariable Long eventId) {
        try {
            return ResponseEntity.ok(service.getEventRegistrations(eventId));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", ex.getMessage()));
        }
    }
}
