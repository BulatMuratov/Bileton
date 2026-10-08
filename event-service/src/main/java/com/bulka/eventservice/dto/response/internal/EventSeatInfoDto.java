package com.bulka.eventservice.dto.internal.response;

import com.bulka.eventservice.model.event.EventSeatStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventSeatInfoDto {
    private UUID eventSeatId;
    private BigDecimal price;
    private EventSeatStatus status;
}
