package com.bulka.eventservice.dto.response;

import com.bulka.eventservice.model.EventSeatStatus;
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
public class EventSeatResponseDto {
    private UUID id;
    private UUID eventId;
    private UUID seatId;
    private EventSeatStatus status;
    private BigDecimal price;
}
