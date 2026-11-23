package org.nlic.connect.dto;

import org.nlic.connect.model.EventRegistration;
import java.time.LocalDateTime;

/** Member-safe representation of an event registration. */
public record EventRegistrationResponse(Long registrationId, Long eventId,
                                        String status, LocalDateTime registrationDate) {
    public static EventRegistrationResponse from(EventRegistration registration) {
        return new EventRegistrationResponse(registration.getRegistrationId(),
                registration.getEventId(), registration.getStatus(),
                registration.getRegistrationDate());
    }
}
