package com.bulka.eventservice.dto.projection.internal;

import com.bulka.eventservice.model.event.EventSeatStatus;

import java.math.BigDecimal;
import java.util.UUID;

public record EventSeatInfoProjection(
        UUID eventSeatId,
        BigDecimal price,
        EventSeatStatus status
) {}
