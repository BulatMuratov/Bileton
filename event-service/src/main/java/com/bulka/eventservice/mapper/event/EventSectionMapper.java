package com.bulka.eventservice.mapper.event;

import com.bulka.eventservice.dto.projection.event.EventSectionDetailsProjection;
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

    public EventSectionDetailsResponseDto toDetailsResponseFromProjection(
            EventSectionDetailsProjection eventSection,
            List<EventSeatDetailsResponseDto> eventSeats) {
        return EventSectionDetailsResponseDto.builder()
                .id(eventSection.id())
                .eventId(eventSection.eventId())
                .sectionId(eventSection.sectionId())
                .name(eventSection.name())
                .x(eventSection.x())
                .y(eventSection.y())
                .width(eventSection.width())
                .height(eventSection.height())
                .rotation(eventSection.rotation())
                .seats(eventSeats)
                .build();
    }

    public EventSectionDetailsResponseDto toDetailsResponseFromEntity(
            EventSection eventSection,
            Section section,
            List<EventSeatDetailsResponseDto> eventSeats) {
        return EventSectionDetailsResponseDto.builder()
                .id(eventSection.getId())
                .eventId(eventSection.getEvent().getId())
                .sectionId(section.getId())
                .name(section.getName())
                .x(section.getLayout().getX())
                .y(section.getLayout().getY())
                .width(section.getLayout().getWidth())
                .height(section.getLayout().getHeight())
                .rotation(section.getLayout().getRotation())
                .seats(eventSeats)
                .build();

    }
}
