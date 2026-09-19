package com.bulka.eventservice.mapper.event;

import com.bulka.eventservice.dto.response.event.EventSeatDetailsResponseDto;
import com.bulka.eventservice.model.event.EventSeat;
import com.bulka.eventservice.model.venue.Seat;
import org.springframework.stereotype.Component;

@Component
public class EventSeatMapper {
    public EventSeatDetailsResponseDto toDetailsResponse(EventSeat eventSeat, Seat seat) {
        return EventSeatDetailsResponseDto.builder()
                .id(eventSeat.getId())
                .eventSectionId(eventSeat.getEventSection().getId())
                .seatId(eventSeat.getSeat().getId())
                .status(eventSeat.getStatus())
                .price(eventSeat.getPrice())
                .rowNumber(seat.getRowNumber())
                .seatNumber(seat.getSeatNumber())
                .x(seat.getX())
                .y(seat.getY())
                .width(seat.getWidth())
                .height(seat.getHeight())
                .rotation(seat.getRotation())
                .build();
    }
}
