package org.nlic.connect.repository;

import org.nlic.connect.model.PrayerRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Provides database access for prayer request records.
 */
public interface PrayerRequestRepository
        extends JpaRepository<PrayerRequest, Long> {

    List<PrayerRequest> findAllByOrderByCreatedAtDesc();
}