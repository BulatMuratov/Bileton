package com.bulka.eventservice.dto.request.event;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventDetailsRequestDto {
    @NotNull
    private UUID venueId;

    @NotBlank
    @Size(min = 1, max = 255)
    private String name;

    @NotBlank
    @Size(min = 1, max = 255)
    private String description;

    @NotNull
    private OffsetDateTime startAt;

    @NotNull
    private OffsetDateTime endAt;

    @NotEmpty
    private List<@Valid EventSectionRequestDto> sections;
}
