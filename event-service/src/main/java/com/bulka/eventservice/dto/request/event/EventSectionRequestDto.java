package com.bulka.eventservice.dto.request.event;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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
public class EventSectionRequestDto {
    @NotNull
    private UUID id;

    @NotEmpty
    private List<@Valid EventSeatRequestDto> seats;
}
