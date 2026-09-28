package org.nlic.connect.controller;

import org.nlic.connect.model.VolunteerOpportunity;
import org.nlic.connect.service.VolunteerOpportunityService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for retrieving currently available volunteer opportunities.
 * Provides the public API used by the frontend to display open opportunities.
 */
@RestController
@RequestMapping("/api/volunteer/opportunities")
@CrossOrigin(origins = "http://localhost:5173")
public class VolunteerOpportunityController {

    private final VolunteerOpportunityService service;

    /**
     * Creates a controller using the configured volunteer opportunity service.
     *
     * @param service the service used to fetch active volunteer opportunities
     */
    public VolunteerOpportunityController(
            VolunteerOpportunityService service) {
        this.service = service;
    }

    /**
     * Retrieves all currently active volunteer opportunities.
     *
     * @return the list of active opportunities available for volunteers
     */
    @GetMapping
    public List<VolunteerOpportunity> getActiveOpportunities() {
        return service.getActiveOpportunities();
    }
}