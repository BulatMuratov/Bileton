package com.bulka.bookingservice.kafka.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentSucceededEvent {
    private UUID eventId;
    private UUID paymentId;
    private UUID bookingId;
    private UUID userId;
    private BigDecimal amount;
    private String currency;
}
