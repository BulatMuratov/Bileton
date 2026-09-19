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
public class SectionRequestDto {
    private String name;
    private Integer x;
    private Integer y;
    private Integer width;
    private Integer height;
    private Integer rotation;
    private List<SeatRequestDto> seats;
}
