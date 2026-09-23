package com.bulka.apigateway.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventDetailsResponse {
    private UUID id;
    private UUID venueId;

    private VenueSizeDto venueSize;

    private String name;
    private String description;
    private OffsetDateTime startAt;
    private OffsetDateTime endAt;
    private EventStatus status;

    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    private List<EventSectionDetailsResponseDto> sections;
}
