package org.nlic.connect.controller;

import org.nlic.connect.model.PrayerRequest;
import org.nlic.connect.service.PrayerRequestService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for the NLIC Connect prayer request module.
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
     * Returns all prayer requests.
     */
    @GetMapping
    public List<PrayerRequest> getPrayerRequests() {
        return prayerRequestService.getAllPrayerRequests();
    }

    /**
     * Creates a new prayer request.
     */
    @PostMapping
    public ResponseEntity<PrayerRequest> createPrayerRequest(
            @RequestBody PrayerRequest prayerRequest) {

        PrayerRequest savedRequest =
                prayerRequestService.createPrayerRequest(prayerRequest);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(savedRequest);
    }
}