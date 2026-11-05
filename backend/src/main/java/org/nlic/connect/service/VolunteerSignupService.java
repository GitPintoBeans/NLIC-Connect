
package org.nlic.connect.service;

import org.nlic.connect.model.User;
import org.nlic.connect.model.VolunteerSignup;
import org.nlic.connect.repository.UserRepository;
import org.nlic.connect.repository.VolunteerSignupRepository;
import org.nlic.connect.model.VolunteerOpportunity;
import org.nlic.connect.repository.VolunteerOpportunityRepository;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Handles volunteer signup business rules and persistence.
 *
 * Supports authenticated member signups and
 * member-specific signup history.
 *
 * @author James Pinto
 */
@Service
public class VolunteerSignupService {

    private final VolunteerSignupRepository signupRepository;
    private final UserRepository userRepository;
    private final VolunteerOpportunityRepository opportunityRepository;

    /**
     * Constructor-based dependency injection.
     */

public VolunteerSignupService(
        VolunteerSignupRepository signupRepository,
        UserRepository userRepository,
        VolunteerOpportunityRepository opportunityRepository) {

    this.signupRepository = signupRepository;
    this.userRepository = userRepository;
    this.opportunityRepository = opportunityRepository;
}

    /**
     * Resolves the authenticated member's database ID
     * using their verified email address.
     *
     * @param authenticatedEmail email from Spring Security
     * @return the authenticated member's user ID
     */
    private Long getAuthenticatedUserId(
            String authenticatedEmail) {

        User member = userRepository
                .findByEmail(authenticatedEmail)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Authenticated member account not found."
                        )
                );

        return member.getUserId();
    }

    /**
     * Retrieves signup history belonging to
     * the currently authenticated member.
     */
    public List<VolunteerSignup> getCurrentMemberSignups(
            String authenticatedEmail) {

        Long userId =
                getAuthenticatedUserId(authenticatedEmail);

        return getUserSignups(userId);
    }

    /**
     * Registers the authenticated member for
     * a volunteer opportunity.
     *
     * The frontend cannot select another member's ID.
     */
    public VolunteerSignup signUpCurrentMember(
            String authenticatedEmail,
            Long opportunityId,
            String notes) {

        Long userId =
                getAuthenticatedUserId(authenticatedEmail);

        return signUp(userId, opportunityId, notes);
    }

    /**
     * Retrieves signup history for a specified user.
     *
     * Retained for authorized administrative operations.
     */
    public List<VolunteerSignup> getUserSignups(Long userId) {

        return signupRepository
                .findByUserIdOrderBySignupDateDesc(userId);
    }

    /**
     * Creates a volunteer signup if no active
     * signup already exists for the same opportunity.
     */
    public VolunteerSignup signUp(
            Long userId,
            Long opportunityId,
            String notes) {

        // Verify that the opportunity exists.
        VolunteerOpportunity opportunity =
                opportunityRepository.findById(opportunityId)
                        .orElseThrow(() ->
                                new IllegalStateException(
                                        "Volunteer opportunity not found."
                                )
                        );

        // Reject inactive opportunities.
        if (!Boolean.TRUE.equals(opportunity.getActive())) {
            throw new IllegalStateException(
                    "This volunteer opportunity is no longer active."
            );
        }

        // Reject opportunities whose start time has passed.
        if (!opportunity.getStartDate().isAfter(LocalDateTime.now())) {
            throw new IllegalStateException(
                    "Registration is closed for this past opportunity."
            );
        }

        boolean alreadySignedUp =
                signupRepository
                        .existsByUserIdAndOpportunityIdAndStatus(
                                userId,
                                opportunityId,
                                "SIGNED_UP"
                        );

        if (alreadySignedUp) {
            throw new IllegalStateException(
                    "You are already signed up for this volunteer opportunity."
            );
        }

        VolunteerSignup signup = new VolunteerSignup();

        signup.setUserId(userId);
        signup.setOpportunityId(opportunityId);
        signup.setStatus("SIGNED_UP");
        signup.setNotes(notes);

        return signupRepository.save(signup);
    }
}