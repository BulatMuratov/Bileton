package com.bulka.bookingservice.dto.projection;

import com.bulka.bookingservice.model.booking.BookingStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record BookingInfoProjection(
        UUID id,
        UUID eventId,
        BookingStatus status,
        BigDecimal totalPrice,
        OffsetDateTime createdAt
) {}
