package com.bulka.eventservice.mapper.event;

import com.bulka.eventservice.dto.response.event.EventSeatDetailsResponseDto;
import com.bulka.eventservice.dto.response.event.EventSectionDetailsResponseDto;
import com.bulka.eventservice.model.event.Event;
import com.bulka.eventservice.model.event.EventSection;
import com.bulka.eventservice.model.venue.Section;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EventSectionMapper {

    public EventSection toEntity(Event event, Section section) {
        return EventSection.builder()
                .event(event)
                .section(section)
                .build();
    }

    public EventSectionDetailsResponseDto toDetailsResponse(
            EventSection eventSection,
            Section section,
            List<EventSeatDetailsResponseDto> eventSeats) {
        return EventSectionDetailsResponseDto.builder()
                .id(eventSection.getId())
                .eventId(eventSection.getEvent().getId())
                .sectionId(section.getId())
                .name(section.getName())
                .x(section.getX())
                .y(section.getY())
                .width(section.getWidth())
                .height(section.getHeight())
                .rotation(section.getRotation())
                .seats(eventSeats)
                .build();

    }
}
