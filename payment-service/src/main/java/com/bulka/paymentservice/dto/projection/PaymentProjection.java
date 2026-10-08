package com.bulka.paymentservice.dto.projection;

import com.bulka.paymentservice.model.PaymentStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record PaymentProjection(
        UUID id,
        UUID bookingId,
        BigDecimal amount,
        String currency,
        PaymentStatus status,
        OffsetDateTime createdAt
) {}
