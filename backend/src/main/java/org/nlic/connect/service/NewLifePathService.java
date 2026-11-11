
package org.nlic.connect.service;

import org.nlic.connect.model.NewLifePathProgress;
import org.nlic.connect.model.NewLifePathStep;
import org.nlic.connect.model.User;

import org.nlic.connect.repository.NewLifePathProgressRepository;
import org.nlic.connect.repository.NewLifePathStepRepository;
import org.nlic.connect.repository.UserRepository;

import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * Provides business logic for managing New Life Path
 * steps and authenticated member progress.
 *
 * @author James Pinto
 */
@Service
public class NewLifePathService {

    private final NewLifePathStepRepository stepRepository;
    private final NewLifePathProgressRepository progressRepository;
    private final UserRepository userRepository;

    /**
     * Constructor-based dependency injection.
     */
    public NewLifePathService(
            NewLifePathStepRepository stepRepository,
            NewLifePathProgressRepository progressRepository,
            UserRepository userRepository) {

        this.stepRepository = stepRepository;
        this.progressRepository = progressRepository;
        this.userRepository = userRepository;
    }

    /**
     * Retrieves all active New Life Path steps
     * in their configured order.
     */
    public List<NewLifePathStep> getActiveSteps() {

        return stepRepository.findByActiveTrueOrderByStepOrderAsc();
    }

    /**
     * Resolves the authenticated member's database ID
     * using the email verified by Spring Security.
     */
    private Long getAuthenticatedUserId(
            String authenticatedEmail) {

        User member = userRepository
                .findByEmail(authenticatedEmail)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Authenticated member account not found."
                        )
                );

        return member.getUserId();
    }

    /**
     * Retrieves progress belonging to the
     * currently authenticated member.
     */
    public List<NewLifePathProgress> getCurrentMemberProgress(
            String authenticatedEmail) {

        Long userId = getAuthenticatedUserId(authenticatedEmail);

        return getUserProgress(userId);
    }

    /**
     * Marks a step complete for the authenticated member.
     *
     * The frontend cannot select another member's ID.
     */
    public NewLifePathProgress completeCurrentMemberStep(
            String authenticatedEmail,
            Long stepId) {

        Long userId = getAuthenticatedUserId(authenticatedEmail);

        return completeStep(userId, stepId);
    }

    /**
     * Retrieves progress history for a specified user.
     *
     * Retained for authorized administrative operations.
     */
    public List<NewLifePathProgress> getUserProgress(Long userId) {

        return progressRepository.findByUserId(userId);
    }

    /**
     * Marks a specified user's step as completed.
     *
     * Verifies that the step exists and reuses
     * an existing progress record when available.
     */
    public NewLifePathProgress completeStep(
            Long userId,
            Long stepId) {

        // Verify that the requested step exists.
        stepRepository.findById(stepId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "New Life Path step not found."
                        )
                );

        // Retrieve existing progress or create a new record.
        NewLifePathProgress progress =
                progressRepository
                        .findByUserIdAndStepId(userId, stepId)
                        .orElseGet(() -> {

                            NewLifePathProgress newProgress =
                                    new NewLifePathProgress();

                            newProgress.setUserId(userId);
                            newProgress.setStepId(stepId);

                            return newProgress;
                        });

        // Do not reset the original completion date
        // if the member has already completed this step.
        if ("COMPLETED".equals(progress.getStatus())) {
            return progress;
        }

        progress.setStatus("COMPLETED");
        progress.setCompletedDate(LocalDate.now());

        // Persist progress in MySQL.
        return progressRepository.save(progress);
    }
}
