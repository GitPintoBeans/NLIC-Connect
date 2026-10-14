
package org.nlic.connect.controller;

import jakarta.validation.Valid;
import org.nlic.connect.dto.RegistrationRequest;
import org.nlic.connect.service.RegistrationService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Map;

/**
 * REST controller for NLIC Connect authentication.
 *
 * Handles member registration requests and
 * returns appropriate HTTP responses.
 *
 * Additional authentication endpoints will be
 * implemented during this development iteration.
 *
 * @author James Pinto
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final RegistrationService registrationService;

    /**
     * Constructor-based dependency injection.
     */
    public AuthController(
            RegistrationService registrationService) {

        this.registrationService = registrationService;
    }

    /**
     * Registers a new NLIC Connect member.
     *
     * @param request validated registration information
     * @return HTTP 201 with the new user's ID,
     *         or HTTP 409 for duplicate registrations
     */
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(
            @Valid @RequestBody RegistrationRequest request) {

        try {

            // Register the new member.
            Long userId =
                    registrationService.registerMember(request);

            // Return confirmation without exposing passwords.
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(Map.of(
                            "message", "Account created successfully.",
                            "userId", userId
                    ));

        } catch (IllegalArgumentException exception) {

            // Handle an email address that is already registered.
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(Map.of(
                            "message", exception.getMessage()
                    ));
        }
    }

    /**
     * Returns information about the currently
     * authenticated NLIC Connect member.
     *
     * This endpoint requires valid authentication.
     *
     * @param authentication the authenticated user
     * @return the member's email and assigned roles
     */
    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getCurrentUser(
            Authentication authentication) {

        // Retrieve the authenticated member's roles.
        var roles = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        // Return safe account information.
        return ResponseEntity.ok(Map.of(
                "email", authentication.getName(),
                "roles", roles,
                "message", "Authentication successful."
        ));
    }

}