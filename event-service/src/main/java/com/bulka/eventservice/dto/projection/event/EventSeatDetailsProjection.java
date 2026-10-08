package com.bulka.eventservice.dto.projection.event;

import com.bulka.eventservice.model.event.EventSeatStatus;

import java.math.BigDecimal;
import java.util.UUID;

public record EventSeatDetailsProjection(
        UUID id,
        UUID eventSectionId,
        UUID seatId,
        EventSeatStatus status,
        BigDecimal price,
        Integer rowNumber,
        Integer seatNumber,
        Integer x,
        Integer y,
        Integer width,
        Integer height,
        Integer rotation
) {
}