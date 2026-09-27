package org.nlic.connect.service;

import org.nlic.connect.model.PrayerRequest;
import org.nlic.connect.repository.PrayerRequestRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Handles business logic for prayer requests.
 */
@Service
public class PrayerRequestService {

    private final PrayerRequestRepository prayerRequestRepository;

    public PrayerRequestService(
            PrayerRequestRepository prayerRequestRepository) {
        this.prayerRequestRepository = prayerRequestRepository;
    }

    /**
     * Retrieves all prayer requests with newest submissions first.
     */
    public List<PrayerRequest> getAllPrayerRequests() {
        return prayerRequestRepository.findAllByOrderByCreatedAtDesc();
    }

    /**
     * Saves a new prayer request.
     */
    public PrayerRequest createPrayerRequest(PrayerRequest prayerRequest) {

        // Milestone 4 development defaults.
        prayerRequest.setStatus("ACTIVE");

        if (prayerRequest.getPrivacyLevel() == null
                || prayerRequest.getPrivacyLevel().isBlank()) {
            prayerRequest.setPrivacyLevel("PRIVATE");
        }

        return prayerRequestRepository.save(prayerRequest);
    }
}