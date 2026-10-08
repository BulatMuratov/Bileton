package com.bulka.eventservice.dto.projection.event;

import java.util.UUID;

public record EventSectionDetailsProjection(
        UUID id,
        UUID eventId,
        UUID sectionId,
        String name,
        Integer x,
        Integer y,
        Integer width,
        Integer height,
        Integer rotation
) {
}