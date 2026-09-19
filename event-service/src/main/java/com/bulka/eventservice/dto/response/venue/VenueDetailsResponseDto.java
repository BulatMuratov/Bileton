package com.bulka.eventservice.dto.response.venue;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VenueDetailsResponseDto {
    private UUID id;
    private String name;
    private String description;
    private Integer width;
    private Integer height;
    private List<SectionResponseDto> sections;
}
