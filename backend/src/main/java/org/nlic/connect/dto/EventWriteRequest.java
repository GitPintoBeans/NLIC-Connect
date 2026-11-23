package org.nlic.connect.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

/** Validated leader-provided event details. Future-date rules are applied by the service
 * separately for newly created events and edits to existing past events. */
public record EventWriteRequest(
        @NotBlank @Size(max = 100) String title,
        String description,
        @NotNull LocalDateTime eventDate,
        @Size(max = 255) String location,
        @Min(1) Integer capacity,
        @NotNull Boolean registrationOpen
) { }
