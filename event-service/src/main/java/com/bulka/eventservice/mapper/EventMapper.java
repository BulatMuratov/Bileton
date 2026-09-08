package com.bulka.eventservice.mapper;

import com.bulka.eventservice.dto.response.EventDetailsResponseDto;
import com.bulka.eventservice.dto.response.EventInfoResponseDto;
import com.bulka.eventservice.dto.response.EventSeatResponseDto;
import com.bulka.eventservice.model.Event;
import com.bulka.eventservice.model.EventSeat;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EventMapper {

    public EventInfoResponseDto toInfoResponse(Event event) {
        return EventInfoResponseDto.builder()
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
            List<EventSeat> eventSeats
    ) {
        List<EventSeatResponseDto> seats = eventSeats.stream()
                .map(this::toEventSeatResponse)
                .toList();

        return EventDetailsResponseDto.builder()
                .id(event.getId())
                .venueId(event.getVenue().getId())
                .name(event.getName())
                .description(event.getDescription())
                .startAt(event.getStartAt())
                .endAt(event.getEndAt())
                .status(event.getStatus())
                .createdAt(event.getCreatedAt())
                .updatedAt(event.getUpdatedAt())
                .seats(seats)
                .build();
    }

    public EventSeatResponseDto toEventSeatResponse(EventSeat eventSeat) {
        return EventSeatResponseDto.builder()
                .id(eventSeat.getId())
                .eventId(eventSeat.getEvent().getId())
                .seatId(eventSeat.getSeat().getId())
                .status(eventSeat.getStatus())
                .price(eventSeat.getPrice())
                .build();
    }
}
