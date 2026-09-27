package org.nlic.connect.repository;

import org.nlic.connect.model.NewLifePathStep;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NewLifePathStepRepository
        extends JpaRepository<NewLifePathStep, Long> {

    List<NewLifePathStep> findByActiveTrueOrderByStepOrderAsc();
}