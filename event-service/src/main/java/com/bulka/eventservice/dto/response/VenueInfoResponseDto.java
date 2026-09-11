package com.bulka.eventservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VenueInfoResponseDto {
    private UUID id;
    private String name;
    private String description;
    private Integer width;
    private Integer height;
}
