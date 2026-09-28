package org.nlic.connect.repository;

import org.nlic.connect.model.VolunteerOpportunity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repository for retrieving active volunteer opportunities.
 */
public interface VolunteerOpportunityRepository
        extends JpaRepository<VolunteerOpportunity, Long> {

    /**
     * Finds active opportunities ordered by start time.
     *
     * @return the active opportunities sorted by start date
     */
    List<VolunteerOpportunity> findByActiveTrueOrderByStartDateAsc();
}