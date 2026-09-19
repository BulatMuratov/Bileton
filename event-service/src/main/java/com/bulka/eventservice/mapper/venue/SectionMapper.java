package com.bulka.eventservice.mapper.venue;

import com.bulka.eventservice.dto.request.venue.SectionRequestDto;
import com.bulka.eventservice.dto.response.venue.SeatResponseDto;
import com.bulka.eventservice.dto.response.venue.SectionResponseDto;
import com.bulka.eventservice.model.venue.Section;
import com.bulka.eventservice.model.venue.Venue;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SectionMapper {
    public Section toEntity(Venue venue, SectionRequestDto request){
        return Section.builder()
                .venue(venue)
                .name(request.getName())
                .x(request.getX())
                .y(request.getY())
                .width(request.getWidth())
                .height(request.getHeight())
                .rotation(request.getRotation())
                .build();
    }

    public SectionResponseDto toResponse(
            Section section,
            List<SeatResponseDto> seats
    ) {
        return SectionResponseDto.builder()
                .id(section.getId())
                .name(section.getName())
                .x(section.getX())
                .y(section.getY())
                .width(section.getWidth())
                .height(section.getHeight())
                .rotation(section.getRotation())
                .seats(seats)
                .build();
    }
}
