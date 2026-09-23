package com.bulka.apigateway.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventSeatDetailsResponseDto {
    private UUID id;
    private UUID eventSectionId;
    private UUID seatId;

    private EventSeatStatus status;
    private BigDecimal price;

    private Integer rowNumber;
    private Integer seatNumber;

    private Integer x;
    private Integer y;
    private Integer width;
    private Integer height;
    private Integer rotation;
}
