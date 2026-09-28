package org.nlic.connect.repository;

import org.nlic.connect.model.NewLifePathProgress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for retrieving and querying New Life Path progress records.
 */
public interface NewLifePathProgressRepository
        extends JpaRepository<NewLifePathProgress, Long> {

    /**
     * Finds all progress records for a specific user.
     *
     * @param userId the identifier of the user
     * @return the list of progress entries for the user
     */
    List<NewLifePathProgress> findByUserId(Long userId);

    /**
     * Finds a single progress record for a given user and step.
     *
     * @param userId the identifier of the user
     * @param stepId the identifier of the step
     * @return the matching progress record if one exists
     */
    Optional<NewLifePathProgress> findByUserIdAndStepId(
            Long userId,
            Long stepId
    );
}