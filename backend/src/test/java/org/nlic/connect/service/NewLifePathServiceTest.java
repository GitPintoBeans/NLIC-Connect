
package org.nlic.connect.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.nlic.connect.model.NewLifePathProgress;
import org.nlic.connect.model.NewLifePathStep;
import org.nlic.connect.model.User;

import org.nlic.connect.repository.NewLifePathProgressRepository;
import org.nlic.connect.repository.NewLifePathStepRepository;
import org.nlic.connect.repository.UserRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Automated regression tests for New Life Path.
 *
 * Uses mocked repositories to test progress tracking
 * without changing the actual MySQL database.
 *
 * @author James Pinto
 */
@ExtendWith(MockitoExtension.class)
class NewLifePathServiceTest {

    @Mock
    private NewLifePathStepRepository stepRepository;

    @Mock
    private NewLifePathProgressRepository progressRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private NewLifePathService newLifePathService;

    /**
     * TC-AUTO-NLP-01
     *
     * Repeating a completed step must preserve
     * the existing record and completion date.
     */
    @Test
    void completedStepShouldNotBeSavedAgain() {

        Long userId = 2L;
        Long stepId = 1L;

        LocalDate originalDate =
                LocalDate.of(2026, 11, 10);

        NewLifePathStep step =
                mock(NewLifePathStep.class);

        NewLifePathProgress existingProgress =
                new NewLifePathProgress();

        existingProgress.setUserId(userId);
        existingProgress.setStepId(stepId);
        existingProgress.setStatus("COMPLETED");
        existingProgress.setCompletedDate(originalDate);

        when(stepRepository.findById(stepId))
                .thenReturn(Optional.of(step));

        when(progressRepository.findByUserIdAndStepId(
                userId, stepId))
                .thenReturn(Optional.of(existingProgress));

        NewLifePathProgress result =
                newLifePathService.completeStep(
                        userId, stepId);

        assertSame(existingProgress, result);

        assertEquals(
                "COMPLETED",
                result.getStatus()
        );

        assertEquals(
                originalDate,
                result.getCompletedDate()
        );

        // An already completed step must not be saved again.
        verify(progressRepository, never())
                .save(any(NewLifePathProgress.class));
    }

    /**
     * TC-AUTO-NLP-02
     *
     * Completing a new step must create
     * a completed progress record.
     */
    @Test
    void newStepShouldCreateCompletedProgress() {

        Long userId = 2L;
        Long stepId = 2L;

        NewLifePathStep step =
                mock(NewLifePathStep.class);

        when(stepRepository.findById(stepId))
                .thenReturn(Optional.of(step));

        when(progressRepository.findByUserIdAndStepId(
                userId, stepId))
                .thenReturn(Optional.empty());

        // Simulate returning the saved progress record.
        when(progressRepository.save(
                any(NewLifePathProgress.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0));

        NewLifePathProgress result =
                newLifePathService.completeStep(
                        userId, stepId);

        assertEquals(userId, result.getUserId());
        assertEquals(stepId, result.getStepId());

        assertEquals(
                "COMPLETED",
                result.getStatus()
        );

        assertEquals(
                LocalDate.now(),
                result.getCompletedDate()
        );

        verify(progressRepository, times(1))
                .save(any(NewLifePathProgress.class));
    }

    /**
     * TC-AUTO-NLP-03
     *
     * The authenticated member's email must
     * resolve to their own database user ID.
     */
    @Test
    void authenticatedMemberShouldRetrieveOwnProgress() {

        String email = "nlic-test-2026@example.com";
        Long userId = 2L;

        User member = mock(User.class);

        when(member.getUserId())
                .thenReturn(userId);

        when(userRepository.findByEmail(email))
                .thenReturn(Optional.of(member));

        NewLifePathProgress progress =
                new NewLifePathProgress();

        progress.setUserId(userId);
        progress.setStepId(1L);
        progress.setStatus("COMPLETED");

        when(progressRepository.findByUserId(userId))
                .thenReturn(List.of(progress));

        List<NewLifePathProgress> results =
                newLifePathService
                        .getCurrentMemberProgress(email);

        assertEquals(1, results.size());

        assertEquals(
                userId,
                results.get(0).getUserId()
        );

        verify(progressRepository)
                .findByUserId(userId);

        verify(progressRepository, never())
                .findByUserId(1L);
    }

    /**
     * TC-AUTO-NLP-04
     *
     * A nonexistent New Life Path step
     * must not create a progress record.
     */
    @Test
    void nonexistentStepShouldBeRejected() {

        Long userId = 2L;
        Long invalidStepId = 999L;

        when(stepRepository.findById(invalidStepId))
                .thenReturn(Optional.empty());

        IllegalArgumentException exception =
                assertThrows(
                    IllegalArgumentException.class,
                    () -> newLifePathService.completeStep(
                            userId, invalidStepId)
                );

        assertEquals(
                "New Life Path step not found.",
                exception.getMessage()
        );

        verify(progressRepository, never())
                .save(any(NewLifePathProgress.class));
    }
}