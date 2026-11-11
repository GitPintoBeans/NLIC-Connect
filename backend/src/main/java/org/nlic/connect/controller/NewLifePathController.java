
package org.nlic.connect.controller;

import org.nlic.connect.model.NewLifePathProgress;
import org.nlic.connect.model.NewLifePathStep;
import org.nlic.connect.service.NewLifePathService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing New Life Path
 * steps and authenticated member progress.
 *
 * @author James Pinto
 */
@RestController
@RequestMapping("/api/new-life-path")
public class NewLifePathController {

    private final NewLifePathService newLifePathService;

    /**
     * Constructor-based dependency injection.
     */
    public NewLifePathController(
            NewLifePathService newLifePathService) {

        this.newLifePathService = newLifePathService;
    }

    /**
     * Retrieves all currently active
     * New Life Path steps.
     */
    @GetMapping("/steps")
    public List<NewLifePathStep> getSteps() {

        return newLifePathService.getActiveSteps();
    }

    /**
     * Retrieves progress belonging to the
     * currently authenticated member.
     *
     * GET /api/new-life-path/progress/me
     */
    @GetMapping("/progress/me")
    public List<NewLifePathProgress> getMyProgress(
            Authentication authentication) {

        return newLifePathService.getCurrentMemberProgress(
                authentication.getName()
        );
    }

    /**
     * Marks a step complete for the
     * currently authenticated member.
     *
     * POST /api/new-life-path/progress/me/{stepId}/complete
     */
    @PostMapping("/progress/me/{stepId}/complete")
    public ResponseEntity<NewLifePathProgress> completeMyStep(
            @PathVariable Long stepId,
            Authentication authentication) {

        NewLifePathProgress progress =
                newLifePathService.completeCurrentMemberStep(
                        authentication.getName(),
                        stepId
                );

        return ResponseEntity.ok(progress);
    }

    /**
     * Retrieves progress for a specified user.
     *
     * Restricted to ADMIN through SecurityConfig.
     */
    @GetMapping("/progress/{userId}")
    public List<NewLifePathProgress> getProgress(
            @PathVariable Long userId) {

        return newLifePathService.getUserProgress(userId);
    }

    /**
     * Marks a step complete for a specified user.
     *
     * Restricted to ADMIN through SecurityConfig.
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
