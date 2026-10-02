package com.bulka.eventservice.dto.response.event;

import com.bulka.eventservice.model.event.EventSeatStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Schema(
        name = "EventSeatDetailsResponse",
        description = """
        Детальная информация о конкретном месте события.
        Содержит статус бронирования, цену и координаты места в секции
        для отрисовки интерактивной схемы зала.
        """
)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventSeatDetailsResponseDto {
    @Schema(
            description = "Уникальный идентификатор места события (event seat)",
            example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890"
    )
    private UUID id;

    @Schema(
            description = "Идентификатор секции события, к которой относится место",
            example = "7c9e6679-7425-40de-944b-e07fc1f90ae7"
    )
    private UUID eventSectionId;

    @Schema(
            description = "Идентификатор физического места на площадке (venue seat)",
            example = "b2c3d4e5-f6a7-8901-bcde-f23456789012"
    )
    private UUID seatId;

    @Schema(
            description = "Текущий статус места: доступно, забронировано, продано и т.д.",
            example = "AVAILABLE"
    )
    private EventSeatStatus status;

    @Schema(
            description = "Цена места для этого события",
            example = "1500.00",
            minimum = "0.00"
    )
    private BigDecimal price;

    @Schema(
            description = "Номер ряда в секции",
            example = "5"
    )
    private Integer rowNumber;

    @Schema(
            description = "Номер места в ряду",
            example = "12"
    )
    private Integer seatNumber;

    @Schema(
            description = "Координата X левого верхнего угла места на схеме зала (в условных единицах)",
            example = "120"
    )
    private Integer x;

    @Schema(
            description = "Координата Y левого верхнего угла места на схеме зала (в условных единицах)",
            example = "340"
    )
    private Integer y;

    @Schema(
            description = "Ширина места на схеме зала (в условных единицах)",
            example = "20"
    )
    private Integer width;

    @Schema(
            description = "Высота места на схеме зала (в условных единицах)",
            example = "20"
    )
    private Integer height;

    @Schema(
            description = "Угол поворота места на схеме зала в градусах (0, 90, 180, 270)",
            example = "0"
    )
    private Integer rotation;
}
