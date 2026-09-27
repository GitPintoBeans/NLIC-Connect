package org.nlic.connect.repository;

import org.nlic.connect.model.VolunteerSignup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VolunteerSignupRepository
        extends JpaRepository<VolunteerSignup, Long> {

    List<VolunteerSignup> findByUserIdOrderBySignupDateDesc(Long userId);

    boolean existsByUserIdAndOpportunityIdAndStatus(
            Long userId,
            Long opportunityId,
            String status
    );
}