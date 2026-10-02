package com.bulka.bookingservice.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Schema(
        name = "EventSeatResponse",
        description = "Информация о забронированном месте в составе бронирования"
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventSeatResponseDto {
    @Schema(
            description = "Уникальный идентификатор места на событии",
            example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890"
    )
    private UUID eventSeatId;

    @Schema(
            description = "Цена конкретного места на момент бронирования",
            example = "1500.00"
    )
    private BigDecimal price;
}

