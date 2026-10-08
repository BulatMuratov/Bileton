package com.bulka.eventservice.mapper.event;

import com.bulka.eventservice.dto.projection.event.EventDetailsProjection;
import com.bulka.eventservice.dto.response.event.VenueSizeDto;
import com.bulka.eventservice.dto.request.event.EventDetailsRequestDto;
import com.bulka.eventservice.dto.response.event.EventDetailsResponseDto;
import com.bulka.eventservice.dto.response.event.EventSectionDetailsResponseDto;
import com.bulka.eventservice.dto.response.event.EventSummaryResponseDto;
import com.bulka.eventservice.model.event.Event;
import com.bulka.eventservice.model.event.EventStatus;
import com.bulka.eventservice.model.venue.Venue;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class EventMapper {

    public Event toEntity(UUID eventId, EventDetailsRequestDto request, Venue venue) {
        return Event.builder()
                .id(eventId)
                .venue(venue)
                .name(request.getName())
                .description(request.getDescription())
                .startAt(request.getStartAt())
                .endAt(request.getEndAt())
                .status(EventStatus.DRAFT)
                .eventType(request.getEventType())
                .build();
    }

    public EventSummaryResponseDto toSummaryResponse(Event event) {
        return EventSummaryResponseDto.builder()
                .id(event.getId())
                .venueId(event.getVenue().getId())
                .name(event.getName())
                .description(event.getDescription())
                .startAt(event.getStartAt())
                .endAt(event.getEndAt())
                .status(event.getStatus())
                .eventType(event.getEventType())
                .createdAt(event.getCreatedAt())
                .updatedAt(event.getUpdatedAt())
                .build();

    }

    public EventDetailsResponseDto toDetailsResponseFromProjection(
            EventDetailsProjection event,
            List<EventSectionDetailsResponseDto> sections,
            VenueSizeDto venueSize
    ) {
        return EventDetailsResponseDto.builder()
                .id(event.id())
                .venueId(event.venueId())
                .venueSize(VenueSizeDto.builder()
                        .width(venueSize.getWidth())
                        .height(venueSize.getHeight())
                        .build())
                .name(event.name())
                .description(event.description())
                .startAt(event.startAt())
                .endAt(event.endAt())
                .status(event.status())
                .eventType(event.eventType())
                .createdAt(event.createdAt())
                .updatedAt(event.updatedAt())
                .sections(sections)
                .build();
    }

    public EventDetailsResponseDto toDetailsResponseFromEntity(
            Event event,
            List<EventSectionDetailsResponseDto> sections,
            VenueSizeDto venueSize
    ) {

        return EventDetailsResponseDto.builder()
                .id(event.getId())
                .venueId(event.getVenue().getId())
                .venueSize(venueSize)
                .name(event.getName())
                .description(event.getDescription())
                .startAt(event.getStartAt())
                .endAt(event.getEndAt())
                .status(event.getStatus())
                .eventType(event.getEventType())
                .createdAt(event.getCreatedAt())
                .updatedAt(event.getUpdatedAt())
                .sections(sections)
                .build();
    }

}
