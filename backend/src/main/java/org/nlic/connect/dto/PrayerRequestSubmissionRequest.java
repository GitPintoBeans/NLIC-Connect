
package org.nlic.connect.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Represents a prayer request submitted by an
 * authenticated NLIC Connect member.
 *
 * Member identity is determined by Spring Security,
 * not by a user ID supplied by the frontend.
 *
 * @author James Pinto
 */
public record PrayerRequestSubmissionRequest(

        @NotBlank(message = "Prayer request title is required")
        @Size(max = 100, message = "Title cannot exceed 100 characters")
        String title,

        @NotBlank(message = "Prayer request message is required")
        String requestText,

        @NotBlank(message = "Privacy selection is required")
        @Pattern(
                regexp = "PUBLIC|PRIVATE",
                message = "Privacy must be PUBLIC or PRIVATE"
        )
        String privacyLevel

) {
}