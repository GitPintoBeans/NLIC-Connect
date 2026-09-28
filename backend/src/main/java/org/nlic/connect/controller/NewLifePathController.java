package org.nlic.connect.controller;

import org.nlic.connect.model.NewLifePathProgress;
import org.nlic.connect.model.NewLifePathStep;
import org.nlic.connect.service.NewLifePathService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing New Life Path steps and user progress.
 * Exposes endpoints for retrieving active steps, viewing a user's progress,
 * and marking a step as completed.
 */
@RestController
@RequestMapping("/api/new-life-path")
public class NewLifePathController {

    private final NewLifePathService newLifePathService;

    /**
     * Creates a controller backed by the supplied New Life Path service.
     *
     * @param newLifePathService the service used to manage steps and progress
     */
    public NewLifePathController(
            NewLifePathService newLifePathService) {
        this.newLifePathService = newLifePathService;
    }

    /**
     * Retrieves all currently active New Life Path steps.
     *
     * @return the list of active steps available to users
     */
    @GetMapping("/steps")
    public List<NewLifePathStep> getSteps() {
        return newLifePathService.getActiveSteps();
    }

    /**
     * Retrieves the progress record for a specific user.
     *
     * @param userId the identifier of the user whose progress is requested
     * @return the list of progress entries for the specified user
     */
    @GetMapping("/progress/{userId}")
    public List<NewLifePathProgress> getProgress(
            @PathVariable Long userId) {

        return newLifePathService.getUserProgress(userId);
    }

    /**
     * Marks a specific New Life Path step as complete for a user.
     *
     * @param userId the identifier of the user completing the step
     * @param stepId the identifier of the step to mark complete
     * @return a response containing the updated progress entry
     */
    @PostMapping("/progress/{userId}/{stepId}/complete")
    public ResponseEntity<NewLifePathProgress> completeStep(
            @PathVariable Long userId,
            @PathVariable Long stepId) {

        return ResponseEntity.ok(
                newLifePathService.completeStep(userId, stepId)
        );
    }
}