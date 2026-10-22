package org.nlic.connect.service;

import org.nlic.connect.dto.PrayerRequestSubmissionRequest;
import org.nlic.connect.model.User;
import org.nlic.connect.repository.UserRepository;
import org.nlic.connect.model.PrayerRequest;
import org.nlic.connect.repository.PrayerRequestRepository;
import org.springframework.stereotype.Service;
import org.nlic.connect.dto.PublicPrayerRequestResponse;

import java.util.List;

/**
 * Handles business logic for prayer requests.
 */
@Service
public class PrayerRequestService {

    private final PrayerRequestRepository prayerRequestRepository;
    private final UserRepository userRepository;

    public PrayerRequestService(
            PrayerRequestRepository prayerRequestRepository,
            UserRepository userRepository) {
        this.prayerRequestRepository = prayerRequestRepository;
        this.userRepository = userRepository;
    }

    /**
     * Retrieves all prayer requests with newest submissions first.
     */
    public List<PrayerRequest> getAllPrayerRequests() {
        return prayerRequestRepository.findAllByOrderByCreatedAtDesc();
    }

    /**
     * Retrieves active public prayer requests and converts
     * them into privacy-safe responses for the Community
     * Prayer Wall.
     *
     * @return public prayer requests without internal user data
     */
    public List<PublicPrayerRequestResponse> getPublicPrayerRequests() {

        return prayerRequestRepository
                .findByPrivacyLevelAndStatusOrderByCreatedAtDesc(
                        "PUBLIC",
                        "ACTIVE"
                )
                .stream()
                .map(prayerRequest ->
                        new PublicPrayerRequestResponse(
                                prayerRequest.getPrayerRequestId(),
                                prayerRequest.getTitle(),
                                prayerRequest.getRequestText(),
                                prayerRequest.getCreatedAt()
                        )
                )
                .toList();
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

    /**
     * Creates a prayer request for the authenticated member.
     *
     * The user ID is retrieved from MySQL using the
     * authenticated email supplied by Spring Security.
     *
     * The frontend cannot select another member's ID.
     *
     * @param request validated prayer request information
     * @param authenticatedEmail email from Spring Security
     * @return saved prayer request
     */
    public PrayerRequest createPrayerRequestForAuthenticatedMember(
            PrayerRequestSubmissionRequest request,
            String authenticatedEmail) {

        // Retrieve the actual authenticated member.
        User member = userRepository
                .findByEmail(authenticatedEmail)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Authenticated member account not found."
                        )
                );

        // Create a new prayer request.
        PrayerRequest prayerRequest = new PrayerRequest();

        // Assign ownership using the database account.
        prayerRequest.setUserId(member.getUserId());

        // Copy the validated submission fields.
        prayerRequest.setTitle(request.title());
        prayerRequest.setRequestText(request.requestText());
        prayerRequest.setPrivacyLevel(request.privacyLevel());

        // New requests start as active.
        prayerRequest.setStatus("ACTIVE");

        // Save the request in MySQL.
        return prayerRequestRepository.save(prayerRequest);
    }

}