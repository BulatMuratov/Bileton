package com.bulka.eventservice.mapper.venue;

import com.bulka.eventservice.dto.projection.venue.SeatDetailsProjection;
import com.bulka.eventservice.dto.request.venue.SeatRequestDto;
import com.bulka.eventservice.dto.response.venue.SeatResponseDto;
import com.bulka.eventservice.model.venue.LayoutPosition;
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
                .layout(LayoutPosition.builder()
                        .x(request.getX())
                        .y(request.getY())
                        .width(request.getWidth())
                        .height(request.getHeight())
                        .rotation(request.getRotation())
                        .build())
                .build();
    }

    public SeatResponseDto toSeatResponse(SeatDetailsProjection seat) {
        return SeatResponseDto.builder()
                .id(seat.id())
                .rowNumber(seat.rowNumber())
                .seatNumber(seat.seatNumber())
                .x(seat.x())
                .y(seat.y())
                .width(seat.width())
                .height(seat.height())
                .rotation(seat.rotation())
                .build();
    }

    public SeatResponseDto toResponse(Seat seat) {
        LayoutPosition layout = seat.getLayout();
        return SeatResponseDto.builder()
                .id(seat.getId())
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
