package com.bulka.eventservice.mapper.venue;

import com.bulka.eventservice.dto.projection.venue.VenueDetailsProjection;
import com.bulka.eventservice.dto.request.venue.VenueDetailsRequestDto;
import com.bulka.eventservice.dto.response.venue.SectionResponseDto;
import com.bulka.eventservice.dto.response.venue.VenueDetailsResponseDto;
import com.bulka.eventservice.dto.response.venue.VenueSummaryResponseDto;
import com.bulka.eventservice.model.venue.Dimensions;
import com.bulka.eventservice.model.venue.Venue;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class VenueMapper {

    public Venue toEntity(UUID venueId, VenueDetailsRequestDto request){
            return Venue.builder()
                    .id(venueId)
                    .name(request.getName())
                    .description(request.getDescription())
                    .dimensions(
                            Dimensions.builder()
                                    .width(request.getWidth())
                                    .height(request.getHeight())
                                    .build()
                    )
                    .build();
    }

    public VenueSummaryResponseDto toVenueSummaryFromEntity(Venue venue) {
        return VenueSummaryResponseDto.builder()
                .id(venue.getId())
                .name(venue.getName())
                .description(venue.getDescription())
                .width(venue.getDimensions().getWidth())
                .height(venue.getDimensions().getHeight())
                .build();
    }


    public VenueSummaryResponseDto toVenueSummaryFromProjection(VenueDetailsProjection projection) {
        return VenueSummaryResponseDto.builder()
                .id(projection.id())
                .name(projection.name())
                .description(projection.description())
                .width(projection.width())
                .height(projection.height())
                .build();
    }

    public VenueDetailsResponseDto toDetailsResponseFromProjection(
            VenueDetailsProjection venue,
            List<SectionResponseDto> sections) {
        return VenueDetailsResponseDto.builder()
                .id(venue.id())
                .name(venue.name())
                .description(venue.description())
                .width(venue.width())
                .height(venue.height())
                .sections(sections)
                .build();
    }

    public VenueDetailsResponseDto toDetailsResponseFromEntity(
            Venue venue,
            List<SectionResponseDto> sections
    ) {
        return VenueDetailsResponseDto.builder()
                .id(venue.getId())
                .name(venue.getName())
                .description(venue.getDescription())
                .width(venue.getDimensions().getWidth())
                .height(venue.getDimensions().getHeight())
                .sections(sections)
                .build();
    }
}
