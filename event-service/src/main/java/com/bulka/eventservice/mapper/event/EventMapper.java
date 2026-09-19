package com.bulka.eventservice.mapper.event;

import com.bulka.eventservice.dto.VenueSizeDto;
import com.bulka.eventservice.dto.request.event.EventDetailsRequestDto;
import com.bulka.eventservice.dto.response.event.EventDetailsResponseDto;
import com.bulka.eventservice.dto.response.event.EventSectionDetailsResponseDto;
import com.bulka.eventservice.dto.response.event.EventSummaryResponseDto;
import com.bulka.eventservice.model.event.Event;
import com.bulka.eventservice.model.event.EventStatus;
import com.bulka.eventservice.model.venue.Venue;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EventMapper {

    public Event toEntity(EventDetailsRequestDto request, Venue venue) {
        return Event.builder()
                .venue(venue)
                .name(request.getName())
                .description(request.getDescription())
                .startAt(request.getStartAt())
                .endAt(request.getEndAt())
                .status(EventStatus.DRAFT)
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
                .createdAt(event.getCreatedAt())
                .updatedAt(event.getUpdatedAt())
                .build();

    }

    public EventDetailsResponseDto toDetailsResponse(
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
                .createdAt(event.getCreatedAt())
                .updatedAt(event.getUpdatedAt())
                .sections(sections)
                .build();
    }

}
