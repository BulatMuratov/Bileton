package com.bulka.eventservice.service;

import com.bulka.eventservice.dto.request.VenueInfoRequestDto;
import com.bulka.eventservice.dto.request.VenueDetailsRequestDto;
import com.bulka.eventservice.dto.response.SeatResponseDto;
import com.bulka.eventservice.dto.response.VenueDetailsResponseDto;
import com.bulka.eventservice.dto.response.VenueInfoResponseDto;
import com.bulka.eventservice.model.Seat;
import com.bulka.eventservice.model.Venue;
import com.bulka.eventservice.repository.SeatRepository;
import com.bulka.eventservice.repository.VenueRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class VenueService {
    private final VenueRepository venueRepository;
    private final SeatRepository seatRepository;

    @Transactional
    public VenueDetailsResponseDto createVenue(VenueDetailsRequestDto venueDetailsRequestDto){
        Venue venue = Venue.builder()
                .name(venueDetailsRequestDto.getName())
                .description(venueDetailsRequestDto.getDescription())
                .width(venueDetailsRequestDto.getWidth())
                .height(venueDetailsRequestDto.getHeight())
                .build();

        Venue savedVenue = venueRepository.save(venue);


        List<Seat> seats = venueDetailsRequestDto.getSeats()
                .stream()
                .map(seatRequestDto ->
                        Seat.builder()
                                .venue(venue)
                                .section(seatRequestDto.getSection())
                                .rowNumber(seatRequestDto.getRowNumber())
                                .seatNumber(seatRequestDto.getSeatNumber())
                                .x(seatRequestDto.getX())
                                .y(seatRequestDto.getY())
                                .width(seatRequestDto.getWidth())
                                .height(seatRequestDto.getHeight())
                                .rotation(seatRequestDto.getRotation())
                                .build())
                .toList();
        List<Seat> savedSeats = seatRepository.saveAll(seats);

        return toResponse(savedVenue, savedSeats);
    }

    @Transactional(readOnly = true)
    public List<VenueInfoResponseDto> getAllVenueInfo(){
        List<Venue> allVenues = venueRepository.findAll();
        return allVenues
                .stream()
                .map(venue -> VenueInfoResponseDto.builder()
                        .id(venue.getId())
                        .name(venue.getName())
                        .description(venue.getDescription())
                        .width(venue.getWidth())
                        .height(venue.getHeight())
                        .build())
                .toList();
    }

    @Transactional(readOnly = true)
    public VenueInfoResponseDto getVenueInfoById(UUID venueId){
        Venue venue =  venueRepository.findById(venueId).orElseThrow(() ->
                new RuntimeException("Venue with id " + venueId + " not found"));

        return VenueInfoResponseDto.builder()
                .id(venue.getId())
                .name(venue.getName())
                .description(venue.getDescription())
                .width(venue.getWidth())
                .height(venue.getHeight())
                .build();
    }

    @Transactional
    public VenueInfoResponseDto updateVenue(UUID venueId, VenueInfoRequestDto venueInfoRequestDto) {
        Venue venue = venueRepository.findById(venueId).orElseThrow(() ->
                new RuntimeException("Venue with id " + venueId + " not found"));

        if(venueInfoRequestDto.getName() != null){
            venue.setName(venueInfoRequestDto.getName());
        }
        if(venueInfoRequestDto.getDescription() != null){
            venue.setDescription(venueInfoRequestDto.getDescription());
        }
        if(venueInfoRequestDto.getWidth() != null){
            venue.setWidth(venueInfoRequestDto.getWidth());
        }
        if(venueInfoRequestDto.getHeight() != null){
            venue.setHeight(venueInfoRequestDto.getHeight());
        }

        return VenueInfoResponseDto.builder()
                .id(venue.getId())
                .name(venue.getName())
                .description(venue.getDescription())
                .width(venue.getWidth())
                .height(venue.getHeight())
                .build();
    }

    @Transactional
    public void deleteVenue(UUID venueId){
        venueRepository.deleteById(venueId);
    }

    @Transactional(readOnly = true)
    public List<SeatResponseDto> getSeatsByVenueId(UUID venueId){
        List<Seat> seats = seatRepository.findAllByVenueId(venueId);

        return seats
                .stream()
                .map(seat -> SeatResponseDto.builder()
                        .id(seat.getId())
                        .venueId(venueId)
                        .section(seat.getSection())
                        .rowNumber(seat.getRowNumber())
                        .seatNumber(seat.getSeatNumber())
                        .x(seat.getX())
                        .y(seat.getY())
                        .width(seat.getWidth())
                        .height(seat.getHeight())
                        .rotation(seat.getRotation())
                        .build())
                .toList();

    }


    private VenueDetailsResponseDto toResponse(Venue venue, List<Seat> seats){
        List<SeatResponseDto> seatResponseDtoList = seats
                .stream()
                .map(seat -> SeatResponseDto.builder()
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
                        .build())
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
