
package org.nlic.connect.controller;

import jakarta.validation.Valid;

import org.nlic.connect.dto.VolunteerSignupRequest;
import org.nlic.connect.model.VolunteerSignup;
import org.nlic.connect.service.VolunteerSignupService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.core.Authentication;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST controller for volunteer signups in NLIC Connect.
 *
 * Supports authenticated member registration,
 * personal signup history, and administrative access.
 *
 * @author James Pinto
 */
@RestController
@RequestMapping("/api/volunteer/signups")
public class VolunteerSignupController {

    private final VolunteerSignupService signupService;

    /**
     * Constructor-based dependency injection.
     */
    public VolunteerSignupController(
            VolunteerSignupService signupService) {

        this.signupService = signupService;
    }

    /**
     * Registers the authenticated member for a
     * volunteer opportunity.
     *
     * Member identity is obtained from Spring Security,
     * not from a user ID supplied by the frontend.
     */
    @PostMapping
    public ResponseEntity<?> createSignup(
            @Valid @RequestBody VolunteerSignupRequest request,
            Authentication authentication) {

        try {
            VolunteerSignup signup =
                    signupService.signUpCurrentMember(
                            authentication.getName(),
                            request.opportunityId(),
                            request.notes()
                    );

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(Map.of(
                            "message",
                            "Volunteer signup completed successfully.",
                            "signupId",
                            signup.getSignupId(),
                            "opportunityId",
                            signup.getOpportunityId()
                    ));

        } catch (IllegalStateException exception) {

            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(Map.of(
                            "message",
                            exception.getMessage()
                    ));
        }
    }

    /**
     * Retrieves volunteer signup history belonging
     * to the currently authenticated member.
     *
     * GET /api/volunteer/signups/me
     */
    @GetMapping("/me")
    public ResponseEntity<List<VolunteerSignup>> getMySignups(
            Authentication authentication) {

        List<VolunteerSignup> signups =
                signupService.getCurrentMemberSignups(
                        authentication.getName()
                );

        return ResponseEntity.ok(signups);
    }

    /**
     * Retrieves signup history for a specified user.
     *
     * This endpoint must remain restricted to ADMIN
     * through Spring Security configuration.
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<VolunteerSignup>> getUserSignups(
            @PathVariable Long userId) {

        return ResponseEntity.ok(
                signupService.getUserSignups(userId)
        );
    }
}
