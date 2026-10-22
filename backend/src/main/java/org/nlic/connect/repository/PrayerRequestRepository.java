
package org.nlic.connect.repository;

import org.nlic.connect.model.PrayerRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Provides database access for prayer request records.
 *
 * Includes queries for retrieving public prayer requests
 * while preserving access to administrative records.
 *
 * @author James Pinto
 */
public interface PrayerRequestRepository
        extends JpaRepository<PrayerRequest, Long> {

    /**
     * Retrieves all prayer requests, newest first.
     *
     * Intended for authorized leadership functions only.
     */
    List<PrayerRequest> findAllByOrderByCreatedAtDesc();

    /**
     * Retrieves prayer requests matching the specified
     * privacy level and status, newest first.
     *
     * Used to retrieve ACTIVE, PUBLIC requests for
     * the NLIC Connect Community Prayer Wall.
     */
    List<PrayerRequest>
        findByPrivacyLevelAndStatusOrderByCreatedAtDesc(
            String privacyLevel,
            String status
        );
}