package org.nlic.connect.repository;

import org.nlic.connect.model.NewLifePathProgress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NewLifePathProgressRepository
        extends JpaRepository<NewLifePathProgress, Long> {

    List<NewLifePathProgress> findByUserId(Long userId);

    Optional<NewLifePathProgress> findByUserIdAndStepId(
            Long userId,
            Long stepId
    );
}