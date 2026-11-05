
package org.nlic.connect.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/**
 * Represents a volunteer signup submitted by an
 * authenticated NLIC Connect member.
 *
 * The member's identity is determined by
 * Spring Security rather than the frontend.
 *
 * @author James Pinto
 */
public record VolunteerSignupRequest(

        @NotNull(message = "Volunteer opportunity is required")
        @Positive(message = "Opportunity ID must be positive")
        Long opportunityId,

        @Size(max = 500, message = "Notes cannot exceed 500 characters")
        String notes

) {
}