package org.nlic.connect.repository;

import org.nlic.connect.model.VolunteerSignup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repository for querying and verifying volunteer signup records.
 */
public interface VolunteerSignupRepository
        extends JpaRepository<VolunteerSignup, Long> {

    /**
     * Finds all signups for a user, sorted newest first.
     *
     * @param userId the identifier of the user
     * @return the user's signups ordered by signup date descending
     */
    List<VolunteerSignup> findByUserIdOrderBySignupDateDesc(Long userId);

    /**
     * Checks whether a user already has a signup for the specified opportunity with a given status.
     *
     * @param userId the identifier of the user
     * @param opportunityId the identifier of the opportunity
     * @param status the signup status to match
     * @return true if a matching signup exists
     */
    boolean existsByUserIdAndOpportunityIdAndStatus(
            Long userId,
            Long opportunityId,
            String status
    );
}