package com.bulka.eventservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeatResponseDto {
    private UUID id;
    private UUID venueId;
    private String section;
    private Integer rowNumber;
    private Integer seatNumber;
    private Integer x;
    private Integer y;
    private Integer width;
    private Integer height;
    private Integer rotation;
}