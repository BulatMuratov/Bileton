package com.bulka.eventservice.dto.request.venue;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VenueDetailsRequestDto {
    private String name;
    private String description;
    private Integer width;
    private Integer height;
    private List<SectionRequestDto> sections;
}
