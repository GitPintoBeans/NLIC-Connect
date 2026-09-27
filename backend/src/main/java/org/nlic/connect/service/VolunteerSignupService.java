package org.nlic.connect.service;

import org.nlic.connect.model.VolunteerSignup;
import org.nlic.connect.repository.VolunteerSignupRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VolunteerSignupService {

    private final VolunteerSignupRepository signupRepository;

    public VolunteerSignupService(
            VolunteerSignupRepository signupRepository
    ) {
        this.signupRepository = signupRepository;
    }

    public List<VolunteerSignup> getUserSignups(Long userId) {
        return signupRepository.findByUserIdOrderBySignupDateDesc(userId);
    }

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