package com.bulka.eventservice.dto.projection.internal;

import java.time.OffsetDateTime;
import java.util.UUID;

public record EventSeatDetailsInfoProjection(
        UUID eventId,
        String eventName,
        OffsetDateTime eventStartAt,
        OffsetDateTime eventEndAt,
        String venueName,
        UUID eventSeatId,
        String sectionName,
        Integer rowNumber,
        Integer seatNumber
) {}
