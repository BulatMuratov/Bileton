package com.bulka.bookingservice.dto.projection;

import java.math.BigDecimal;
import java.util.UUID;

public record BookingPaymentDetailsProjection(
        UUID bookingId,
        UUID userId,
        BigDecimal amount
) {
}
