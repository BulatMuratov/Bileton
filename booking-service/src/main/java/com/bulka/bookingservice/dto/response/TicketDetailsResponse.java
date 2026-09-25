package com.bulka.bookingservice.dto.response;

import com.bulka.bookingservice.model.ticket.TicketStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TicketDetailsResponse {
    private UUID id;
    private String ticketNumber;

    private UUID bookingId;

    private UUID eventId;
    private String eventName;
    private OffsetDateTime eventStartAt;
    private OffsetDateTime eventEndAt;

    private String venueName;

    private String sectionName;
    private Integer rowNumber;
    private Integer seatNumber;

    private BigDecimal price;
    private String currency;

    private TicketStatus status;
    private OffsetDateTime createdAt;
}
