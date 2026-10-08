package com.bulka.eventservice.mapper.venue;

import com.bulka.eventservice.dto.projection.venue.SectionDetailsProjection;
import com.bulka.eventservice.dto.request.venue.SectionRequestDto;
import com.bulka.eventservice.dto.response.venue.SeatResponseDto;
import com.bulka.eventservice.dto.response.venue.SectionResponseDto;
import com.bulka.eventservice.model.venue.LayoutPosition;
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
                .layout(LayoutPosition.builder()
                        .x(request.getX())
                        .y(request.getY())
                        .width(request.getWidth())
                        .height(request.getHeight())
                        .rotation(request.getRotation())
                        .build())
                .build();
    }

    public SectionResponseDto toResponseFromProjection(
            SectionDetailsProjection section,
            List<SeatResponseDto> seats){
        return SectionResponseDto.builder()
                .id(section.id())
                .name(section.name())
                .x(section.x())
                .y(section.y())
                .width(section.width())
                .height(section.height())
                .rotation(section.rotation())
                .seats(seats)
                .build();
    }

    public SectionResponseDto toResponseFromEntity(
            Section section,
            List<SeatResponseDto> seats
    ) {
        LayoutPosition layout = section.getLayout();
        return SectionResponseDto.builder()
                .id(section.getId())
                .name(section.getName())
                .x(layout.getX())
                .y(layout.getY())
                .width(layout.getWidth())
                .height(layout.getHeight())
                .rotation(layout.getRotation())
                .seats(seats)
                .build();
    }
}
