package com.bulka.eventservice.dto.projection.venue;

import java.util.UUID;

public record SeatDetailsProjection(
        UUID id,
        UUID sectionId,
        Integer rowNumber,
        Integer seatNumber,
        Integer x,
        Integer y,
        Integer width,
        Integer height,
        Integer rotation
) {
}
