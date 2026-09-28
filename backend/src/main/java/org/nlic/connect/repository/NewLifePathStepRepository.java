package org.nlic.connect.repository;

import org.nlic.connect.model.NewLifePathStep;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repository for querying New Life Path steps.
 */
public interface NewLifePathStepRepository
        extends JpaRepository<NewLifePathStep, Long> {

    /**
     * Retrieves all active steps ordered by their display sequence.
     *
     * @return the active steps sorted by step order
     */
    List<NewLifePathStep> findByActiveTrueOrderByStepOrderAsc();
}