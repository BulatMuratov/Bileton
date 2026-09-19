package com.bulka.eventservice.dto.response.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventSectionDetailsResponseDto {
    private UUID id;
    private UUID eventId;
    private UUID sectionId;

    private String name;

    private Integer x;
    private Integer y;
    private Integer width;
    private Integer height;
    private Integer rotation;

    private List<EventSeatDetailsResponseDto> seats;
}
