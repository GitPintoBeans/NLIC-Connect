package org.nlic.connect.controller;

import jakarta.validation.Valid;

import org.nlic.connect.dto.PrayerRequestSubmissionRequest;
import org.nlic.connect.dto.PublicPrayerRequestResponse;
import org.nlic.connect.model.PrayerRequest;
import org.nlic.connect.service.PrayerRequestService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST controller for the NLIC Connect prayer request module.
 *
 * Supports public prayer viewing, leadership access,
 * and secure submissions by authenticated members.
 *
 * @author James Pinto
 */
@RestController
@RequestMapping("/api/prayer-requests")
public class PrayerRequestController {

    private final PrayerRequestService prayerRequestService;

    public PrayerRequestController(
            PrayerRequestService prayerRequestService) {

        this.prayerRequestService = prayerRequestService;
    }

    /**
     * Returns active public prayer requests.
     *
     * Does not expose private requests or member IDs.
     */
    @GetMapping("/public")
    public List<PublicPrayerRequestResponse> getPublicPrayerRequests() {

        return prayerRequestService.getPublicPrayerRequests();
    }

    /**
     * Returns all prayer requests.
     *
     * Access is restricted to ADMIN and LEADER roles
     * through Spring Security.
     */
    @GetMapping
    public List<PrayerRequest> getPrayerRequests() {

        return prayerRequestService.getAllPrayerRequests();
    }

    /**
     * Creates a prayer request for the authenticated member.
     *
     * Member identity comes from Spring Security rather
     * than a user ID supplied by the frontend.
     */
    @PostMapping
    public ResponseEntity<Map<String, String>> createPrayerRequest(
            @Valid @RequestBody PrayerRequestSubmissionRequest request,
            Authentication authentication) {

        prayerRequestService.createPrayerRequestForAuthenticatedMember(
                request,
                authentication.getName()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(Map.of(
                        "message", "Prayer request submitted successfully."
                ));
    }
}