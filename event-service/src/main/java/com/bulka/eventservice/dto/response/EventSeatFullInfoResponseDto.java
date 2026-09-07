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
@AllArgsConstructor
@NoArgsConstructor
public class EventSeatFullInfoResponseDto {
    private UUID id;
    private String section;
    private Integer rowNumber;
    private Integer seatNumber;
    private Integer x;
    private Integer y;
    private Integer width;
    private Integer height;
    private Integer rotation;
    private BigDecimal price;
    private EventSeatStatus status;
}
