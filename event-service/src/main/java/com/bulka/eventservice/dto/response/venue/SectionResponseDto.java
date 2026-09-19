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
public class SectionResponseDto {
    private UUID id;
    private String name;
    private Integer x;
    private Integer y;
    private Integer width;
    private Integer height;
    private Integer rotation;
    private List<SeatResponseDto> seats;
}