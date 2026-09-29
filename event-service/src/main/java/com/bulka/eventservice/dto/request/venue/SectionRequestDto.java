package com.bulka.eventservice.dto.request.venue;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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
    @NotBlank
    private String name;

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

    @NotEmpty
    private List<@Valid SeatRequestDto> seats;
}
