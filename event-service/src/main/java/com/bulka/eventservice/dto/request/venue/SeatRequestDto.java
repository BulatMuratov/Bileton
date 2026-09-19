package com.bulka.eventservice.dto.request.venue;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeatRequestDto {
    private Integer rowNumber;
    private Integer seatNumber;
    private Integer x;
    private Integer y;
    private Integer width;
    private Integer height;
    private Integer rotation;
}
