package com.bulka.eventservice.dto.request.event;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventInfoRequestDto {
    private String name;
    private String description;
    private OffsetDateTime startAt;
    private OffsetDateTime endAt;
}
