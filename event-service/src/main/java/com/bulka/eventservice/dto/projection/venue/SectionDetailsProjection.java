package com.bulka.eventservice.dto.projection.venue;

import java.util.UUID;

public record SectionDetailsProjection(
        UUID id,
        String name,
        Integer x,
        Integer y,
        Integer width,
        Integer height,
        Integer rotation
) {
}
