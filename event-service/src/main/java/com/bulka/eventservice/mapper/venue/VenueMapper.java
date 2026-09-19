package com.bulka.eventservice.mapper.venue;

import com.bulka.eventservice.dto.request.venue.VenueDetailsRequestDto;
import com.bulka.eventservice.dto.response.venue.SectionResponseDto;
import com.bulka.eventservice.dto.response.venue.VenueDetailsResponseDto;
import com.bulka.eventservice.dto.response.venue.VenueSummaryResponseDto;
import com.bulka.eventservice.model.venue.Venue;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class VenueMapper {

    public Venue toEntity(VenueDetailsRequestDto request){
            return Venue.builder()
                    .name(request.getName())
                    .description(request.getDescription())
                    .width(request.getWidth())
                    .height(request.getHeight())
                    .build();
    }

    public VenueSummaryResponseDto toVenueSummary(Venue venue) {
        return VenueSummaryResponseDto.builder()
                .id(venue.getId())
                .name(venue.getName())
                .description(venue.getDescription())
                .width(venue.getWidth())
                .height(venue.getHeight())
                .build();
    }

    public VenueDetailsResponseDto toDetailsResponse(
            Venue venue,
            List<SectionResponseDto> sections
    ) {
        return VenueDetailsResponseDto.builder()
                .id(venue.getId())
                .name(venue.getName())
                .description(venue.getDescription())
                .width(venue.getWidth())
                .height(venue.getHeight())
                .sections(sections)
                .build();
    }
}
