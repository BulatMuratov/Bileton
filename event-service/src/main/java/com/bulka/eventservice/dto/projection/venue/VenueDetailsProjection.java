package com.bulka.eventservice.dto.projection.venue;

import java.util.UUID;

public record VenueDetailsProjection(
        UUID id,
        String name,
        String description,
        Integer width,
        Integer height
) {
}
