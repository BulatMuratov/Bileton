package com.bulka.eventservice.dto.request.venue;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeatRequestDto {
    @NotNull
    @Positive
    private Integer rowNumber;

    @NotNull
    @Positive
    private Integer seatNumber;

    @NotNull
    private Integer x;

    @NotNull
    private Integer y;

    @NotNull
    @Positive
    private Integer width;

    @NotNull
    @Positive
    private Integer height;

    @NotNull
    private Integer rotation;
}
