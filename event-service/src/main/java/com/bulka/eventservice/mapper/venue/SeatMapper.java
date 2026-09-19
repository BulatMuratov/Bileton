package com.bulka.eventservice.mapper.venue;

import com.bulka.eventservice.dto.request.venue.SeatRequestDto;
import com.bulka.eventservice.dto.response.venue.SeatResponseDto;
import com.bulka.eventservice.model.venue.Seat;
import com.bulka.eventservice.model.venue.Section;
import org.springframework.stereotype.Component;

@Component
public class SeatMapper {

    public Seat toEntity(Section section, SeatRequestDto request){
        return Seat.builder()
                .section(section)
                .rowNumber(request.getRowNumber())
                .seatNumber(request.getSeatNumber())
                .x(request.getX())
                .y(request.getY())
                .width(request.getWidth())
                .height(request.getHeight())
                .rotation(request.getRotation())
                .build();
    }

    public SeatResponseDto toResponse(Seat seat) {
        return SeatResponseDto.builder()
                .id(seat.getId())
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
