package org.nlic.connect.repository;

import org.nlic.connect.model.VolunteerOpportunity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VolunteerOpportunityRepository
        extends JpaRepository<VolunteerOpportunity, Long> {

    List<VolunteerOpportunity> findByActiveTrueOrderByStartDateAsc();
}