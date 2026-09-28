package org.nlic.connect.service;

import org.nlic.connect.model.VolunteerOpportunity;
import org.nlic.connect.repository.VolunteerOpportunityRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Provides business logic for retrieving currently available volunteer opportunities.
 */
@Service
public class VolunteerOpportunityService {

    private final VolunteerOpportunityRepository repository;

    /**
     * Creates the service with the volunteer opportunity repository.
     *
     * @param repository the repository used to query opportunities
     */
    public VolunteerOpportunityService(
            VolunteerOpportunityRepository repository) {
        this.repository = repository;
    }

    /**
     * Retrieves all active opportunities sorted by their upcoming start date.
     *
     * @return the list of active volunteer opportunities
     */
    public List<VolunteerOpportunity> getActiveOpportunities() {
        return repository.findByActiveTrueOrderByStartDateAsc();
    }
}