package com.bulka.eventservice.dto.response;

import com.bulka.eventservice.model.EventStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventDetailsResponseDto {
    private UUID id;
    private UUID venueId;
    private String name;
    private String description;
    private OffsetDateTime startAt;
    private OffsetDateTime endAt;
    private EventStatus status;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private List<EventSeatResponseDto> seats;
}
