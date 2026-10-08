package com.bulka.bookingservice.dto.projection;

import java.math.BigDecimal;
import java.util.UUID;

public record BookingItemProjection(
        UUID eventSeatId,
        BigDecimal price
) {
}
