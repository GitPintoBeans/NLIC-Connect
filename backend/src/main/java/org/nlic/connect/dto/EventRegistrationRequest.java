package org.nlic.connect.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/** The frontend supplies an event ID, never a member ID. */
public record EventRegistrationRequest(@NotNull @Positive Long eventId) { }
