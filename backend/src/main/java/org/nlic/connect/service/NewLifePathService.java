package org.nlic.connect.service;

import org.nlic.connect.model.NewLifePathProgress;
import org.nlic.connect.model.NewLifePathStep;
import org.nlic.connect.repository.NewLifePathProgressRepository;
import org.nlic.connect.repository.NewLifePathStepRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class NewLifePathService {

    private final NewLifePathStepRepository stepRepository;
    private final NewLifePathProgressRepository progressRepository;

    public NewLifePathService(
            NewLifePathStepRepository stepRepository,
            NewLifePathProgressRepository progressRepository) {

        this.stepRepository = stepRepository;
        this.progressRepository = progressRepository;
    }

    public List<NewLifePathStep> getActiveSteps() {
        return stepRepository.findByActiveTrueOrderByStepOrderAsc();
    }

    public List<NewLifePathProgress> getUserProgress(Long userId) {
        return progressRepository.findByUserId(userId);
    }

    public NewLifePathProgress completeStep(Long userId, Long stepId) {

        // Verify that the requested step actually exists.
        stepRepository.findById(stepId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "New Life Path step not found."
                        )
                );

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

        progress.setStatus("COMPLETED");
        progress.setCompletedDate(LocalDate.now());

        return progressRepository.save(progress);
    }
}