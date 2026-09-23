package com.bulka.bookingservice.dto.response;

import com.bulka.bookingservice.model.TicketStatus;
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
public class TicketSummaryResponse {
    private UUID id;
    private String ticketNumber;
    private String eventName;
    private OffsetDateTime eventStartAt;
    private OffsetDateTime eventEndAt;
    private String venueName;
    private String sectionName;
    private Integer rowNumber;
    private Integer seatNumber;
    private BigDecimal price;
    private TicketStatus status;
}
