package org.nlic.connect.service;

import org.nlic.connect.model.VolunteerSignup;
import org.nlic.connect.repository.VolunteerSignupRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Handles volunteer signup business rules and persistence.
 */
@Service
public class VolunteerSignupService {

    private final VolunteerSignupRepository signupRepository;

    /**
     * Creates the signup service with the required repository.
     *
     * @param signupRepository the repository used to persist volunteer signups
     */
    public VolunteerSignupService(
            VolunteerSignupRepository signupRepository
    ) {
        this.signupRepository = signupRepository;
    }

    /**
     * Retrieves a user's signup history in reverse chronological order.
     *
     * @param userId the identifier of the user
     * @return the user's recent signups
     */
    public List<VolunteerSignup> getUserSignups(Long userId) {
        return signupRepository.findByUserIdOrderBySignupDateDesc(userId);
    }

    /**
     * Creates a new volunteer signup if the user is not already signed up.
     *
     * @param userId the identifier of the user signing up
     * @param opportunityId the opportunity being joined
     * @param notes optional notes supplied with the signup
     * @return the saved signup record
     */
    public VolunteerSignup signUp(
            Long userId,
            Long opportunityId,
            String notes
    ) {

        boolean alreadySignedUp =
                signupRepository.existsByUserIdAndOpportunityIdAndStatus(
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