
package org.nlic.connect.service;

import org.nlic.connect.model.VolunteerOpportunity;
import org.nlic.connect.repository.VolunteerOpportunityRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Provides business logic for retrieving available
 * volunteer opportunities in NLIC Connect.
 *
 * Only active opportunities with upcoming start dates
 * are displayed to members.
 *
 * @author James Pinto
 */
@Service
public class VolunteerOpportunityService {

    private final VolunteerOpportunityRepository repository;

    public VolunteerOpportunityService(
            VolunteerOpportunityRepository repository) {

        this.repository = repository;
    }

    /**
     * Retrieves active volunteer opportunities
     * scheduled to begin in the future.
     *
     * @return upcoming opportunities sorted by start date
     */
    public List<VolunteerOpportunity> getActiveOpportunities() {

        return repository
                .findByActiveTrueAndStartDateAfterOrderByStartDateAsc(
                        LocalDateTime.now()
                );
    }
}
