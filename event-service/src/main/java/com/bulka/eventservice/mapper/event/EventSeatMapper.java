package com.bulka.eventservice.mapper.event;

import com.bulka.eventservice.dto.projection.event.EventSeatDetailsProjection;
import com.bulka.eventservice.dto.response.event.EventSeatDetailsResponseDto;
import com.bulka.eventservice.model.event.EventSeat;
import com.bulka.eventservice.model.venue.LayoutPosition;
import com.bulka.eventservice.model.venue.Seat;
import org.springframework.stereotype.Component;

@Component
public class EventSeatMapper {
    public EventSeatDetailsResponseDto toDetailsResponseFromProjection(EventSeatDetailsProjection eventSeat){
        return EventSeatDetailsResponseDto.builder()
                .id(eventSeat.id())
                .eventSectionId(eventSeat.eventSectionId())
                .seatId(eventSeat.seatId())
                .status(eventSeat.status())
                .price(eventSeat.price())
                .rowNumber(eventSeat.rowNumber())
                .seatNumber(eventSeat.seatNumber())
                .x(eventSeat.x())
                .y(eventSeat.y())
                .width(eventSeat.width())
                .height(eventSeat.height())
                .rotation(eventSeat.rotation())
                .build();
    }

    public EventSeatDetailsResponseDto toDetailsResponseFromEntity(EventSeat eventSeat, Seat seat) {
        LayoutPosition layout = seat.getLayout();
        return EventSeatDetailsResponseDto.builder()
                .id(eventSeat.getId())
                .eventSectionId(eventSeat.getEventSection().getId())
                .seatId(eventSeat.getSeat().getId())
                .status(eventSeat.getStatus())
                .price(eventSeat.getPrice())
                .rowNumber(seat.getRowNumber())
                .seatNumber(seat.getSeatNumber())
                .x(layout.getX())
                .y(layout.getY())
                .width(layout.getWidth())
                .height(layout.getHeight())
                .rotation(layout.getRotation())
                .build();
    }
}
