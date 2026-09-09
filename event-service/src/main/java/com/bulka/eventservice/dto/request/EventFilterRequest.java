package com.bulka.eventservice.dto.request;

import com.bulka.eventservice.model.EventStatus;
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
public class EventFilterRequest {
    private EventStatus status;
    private UUID venueId;
    private OffsetDateTime from;
    private OffsetDateTime to;
}
