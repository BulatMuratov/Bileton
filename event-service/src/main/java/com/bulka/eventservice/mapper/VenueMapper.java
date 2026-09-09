package com.bulka.eventservice.mapper;

import com.bulka.eventservice.dto.response.SeatResponseDto;
import com.bulka.eventservice.dto.response.VenueDetailsResponseDto;
import com.bulka.eventservice.dto.response.VenueInfoResponseDto;
import com.bulka.eventservice.model.Seat;
import com.bulka.eventservice.model.Venue;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class VenueMapper {

    public VenueInfoResponseDto toVenueInfo(Venue venue) {
        return VenueInfoResponseDto.builder()
                .id(venue.getId())
                .name(venue.getName())
                .description(venue.getDescription())
                .width(venue.getWidth())
                .height(venue.getHeight())
                .build();
    }


    public SeatResponseDto toSeatResponse(Venue venue, Seat seat) {
        return SeatResponseDto.builder()
                .id(seat.getId())
                .venueId(venue.getId())
                .section(seat.getSection())
                .rowNumber(seat.getRowNumber())
                .seatNumber(seat.getSeatNumber())
                .x(seat.getX())
                .y(seat.getY())
                .width(seat.getWidth())
                .height(seat.getHeight())
                .rotation(seat.getRotation())
                .build();
    }

    public VenueDetailsResponseDto toVenueDetails(Venue venue, List<Seat> seats) {
        List<SeatResponseDto> seatResponseDtoList = seats.stream()
                .map(seat -> toSeatResponse(venue, seat))
                .toList();

        return VenueDetailsResponseDto.builder()
                .id(venue.getId())
                .name(venue.getName())
                .description(venue.getDescription())
                .width(venue.getWidth())
                .height(venue.getHeight())
                .seats(seatResponseDtoList)
                .build();
    }
}
