package com.bulka.bookingservice.dto.projection;

import com.bulka.bookingservice.model.ticket.TicketStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record TicketDetailsProjection(
        UUID id,
        String ticketNumber,
        UUID bookingId,
        UUID eventId,
        String eventName,
        OffsetDateTime eventStartAt,
        OffsetDateTime eventEndAt,
        String venueName,
        String sectionName,
        Integer rowNumber,
        Integer seatNumber,
        BigDecimal price,
        String currency,
        TicketStatus status,
        OffsetDateTime createdAt
) {
}
