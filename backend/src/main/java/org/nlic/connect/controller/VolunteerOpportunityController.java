package org.nlic.connect.controller;

import org.nlic.connect.model.VolunteerOpportunity;
import org.nlic.connect.service.VolunteerOpportunityService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/volunteer/opportunities")
@CrossOrigin(origins = "http://localhost:5173")
public class VolunteerOpportunityController {

    private final VolunteerOpportunityService service;

    public VolunteerOpportunityController(
            VolunteerOpportunityService service) {
        this.service = service;
    }

    @GetMapping
    public List<VolunteerOpportunity> getActiveOpportunities() {
        return service.getActiveOpportunities();
    }
}