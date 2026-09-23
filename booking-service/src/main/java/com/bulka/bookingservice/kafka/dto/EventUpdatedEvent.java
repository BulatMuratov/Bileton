package com.bulka.bookingservice.kafka.dto;

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
public class EventUpdatedEvent {
    private UUID eventId;
    private String name;
    private OffsetDateTime startAt;
    private OffsetDateTime endAt;
}
