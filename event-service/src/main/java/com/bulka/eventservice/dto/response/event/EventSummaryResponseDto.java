package com.bulka.eventservice.dto.response.event;

import com.bulka.eventservice.model.event.EventStatus;
import com.bulka.eventservice.model.event.EventType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventSummaryResponseDto {
    private UUID id;
    private UUID venueId;
    private String name;
    private String description;
    private OffsetDateTime startAt;
    private OffsetDateTime endAt;
    private EventStatus status;
    private EventType eventType;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
