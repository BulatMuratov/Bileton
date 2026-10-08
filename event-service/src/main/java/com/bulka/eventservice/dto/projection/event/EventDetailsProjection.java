package com.bulka.eventservice.dto.projection.event;

import com.bulka.eventservice.model.event.EventStatus;
import com.bulka.eventservice.model.event.EventType;

import java.time.OffsetDateTime;
import java.util.UUID;

public record EventDetailsProjection(
        UUID id,
        UUID venueId,
        Integer venueWidth,
        Integer venueHeight,
        String name,
        String description,
        OffsetDateTime startAt,
        OffsetDateTime endAt,
        EventStatus status,
        EventType eventType,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}