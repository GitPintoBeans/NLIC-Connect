package org.nlic.connect.controller;

import org.nlic.connect.model.NewLifePathProgress;
import org.nlic.connect.model.NewLifePathStep;
import org.nlic.connect.service.NewLifePathService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/new-life-path")
public class NewLifePathController {

    private final NewLifePathService newLifePathService;

    public NewLifePathController(
            NewLifePathService newLifePathService) {
        this.newLifePathService = newLifePathService;
    }

    @GetMapping("/steps")
    public List<NewLifePathStep> getSteps() {
        return newLifePathService.getActiveSteps();
    }

    @GetMapping("/progress/{userId}")
    public List<NewLifePathProgress> getProgress(
            @PathVariable Long userId) {

        return newLifePathService.getUserProgress(userId);
    }

    @PostMapping("/progress/{userId}/{stepId}/complete")
    public ResponseEntity<NewLifePathProgress> completeStep(
            @PathVariable Long userId,
            @PathVariable Long stepId) {

        return ResponseEntity.ok(
                newLifePathService.completeStep(userId, stepId)
        );
    }
}