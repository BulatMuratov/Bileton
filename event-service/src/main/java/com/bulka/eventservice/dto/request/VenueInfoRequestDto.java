package com.bulka.eventservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VenueInfoRequestDto {
    private String name;
    private String description;
    private Integer width;
    private Integer height;
}
