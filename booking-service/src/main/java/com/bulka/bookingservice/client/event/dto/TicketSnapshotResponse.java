package com.bulka.bookingservice.client.event.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TicketSnapshotResponse {
    private UUID eventId;
    private String eventName;
    private OffsetDateTime eventStartAt;
    private OffsetDateTime eventEndAt;
    private String venueName;
    private UUID eventSeatId;
    private String sectionName;
    private Integer rowNumber;
    private Integer seatNumber;
}
