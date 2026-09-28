package org.nlic.connect.controller;

import org.nlic.connect.model.VolunteerSignup;
import org.nlic.connect.service.VolunteerSignupService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST controller for managing volunteer signups.
 * Handles creating new volunteer registrations and retrieving a user's signup history.
 */
@RestController
@RequestMapping("/api/volunteer/signups")
@CrossOrigin(origins = "http://localhost:5173")
public class VolunteerSignupController {

    private final VolunteerSignupService signupService;

    /**
     * Creates a controller backed by the volunteer signup service.
     *
     * @param signupService the service used to process volunteer signup operations
     */
    public VolunteerSignupController(
            VolunteerSignupService signupService
    ) {
        this.signupService = signupService;
    }

    /**
     * Creates a volunteer signup for a user and opportunity.
     *
     * @param request a map containing userId, opportunityId, and optional notes
     * @return an HTTP response containing the created signup, or a validation error message
     */
    @PostMapping
    public ResponseEntity<?> createSignup(
            @RequestBody Map<String, Object> request
    ) {
        try {
            // Parse required identifiers from the incoming JSON request.
            Long userId =
                    Long.valueOf(request.get("userId").toString());

            Long opportunityId =
                    Long.valueOf(request.get("opportunityId").toString());

            // Notes are optional and may be omitted from the request.
            String notes = request.get("notes") != null
                    ? request.get("notes").toString()
                    : null;

            VolunteerSignup signup =
                    signupService.signUp(
                            userId,
                            opportunityId,
                            notes
                    );

            return ResponseEntity.ok(signup);

        } catch (IllegalStateException e) {
            // Surface business validation issues as a 400 response.
            return ResponseEntity.badRequest()
                    .body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * Retrieves all signup records associated with a single user.
     *
     * @param userId the ID of the user whose signups are requested
     * @return an HTTP response containing the user's signup list
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserSignups(
            @PathVariable Long userId
    ) {
        return ResponseEntity.ok(
                signupService.getUserSignups(userId)
        );
    }
}