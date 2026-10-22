
package org.nlic.connect.dto;

import java.time.LocalDateTime;

/**
 * Represents a public prayer request displayed
 * on the NLIC Connect Community Prayer Wall.
 *
 * Excludes member IDs and other internal
 * database information.
 *
 * @author James Pinto
 */
public record PublicPrayerRequestResponse(

        Long prayerRequestId,

        String title,

        String requestText,

        LocalDateTime createdAt

) {
}