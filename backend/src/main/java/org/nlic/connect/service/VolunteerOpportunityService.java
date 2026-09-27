package org.nlic.connect.service;

import org.nlic.connect.model.VolunteerOpportunity;
import org.nlic.connect.repository.VolunteerOpportunityRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VolunteerOpportunityService {

    private final VolunteerOpportunityRepository repository;

    public VolunteerOpportunityService(
            VolunteerOpportunityRepository repository) {
        this.repository = repository;
    }

    public List<VolunteerOpportunity> getActiveOpportunities() {
        return repository.findByActiveTrueOrderByStartDateAsc();
    }
}