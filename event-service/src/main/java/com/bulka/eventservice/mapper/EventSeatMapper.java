package com.bulka.eventservice.mapper;

import com.bulka.eventservice.dto.response.EventSeatFullInfoResponseDto;
import com.bulka.eventservice.model.EventSeat;
import com.bulka.eventservice.model.Seat;
import org.springframework.stereotype.Component;

@Component
public class EventSeatMapper {
    public EventSeatFullInfoResponseDto toFullInfoResponse(Seat seat, EventSeat eventSeat) {
        return EventSeatFullInfoResponseDto.builder()
                .id(eventSeat.getId())
                .section(seat.getSection())
                .rowNumber(seat.getRowNumber())
                .seatNumber(seat.getSeatNumber())
                .x(seat.getX())
                .y(seat.getY())
                .width(seat.getWidth())
                .height(seat.getHeight())
                .rotation(seat.getRotation())
                .price(eventSeat.getPrice())
                .status(eventSeat.getStatus())
                .build();
    }
}
