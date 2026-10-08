package com.bulka.apigateway.dto.event;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Schema(description = "Детальная информация о конкретном месте в секции")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventSeatDetailsResponseDto {
    @Schema(description = "Уникальный идентификатор места в контексте события", example = "c3d4e5f6-a7b8-9012-cdef-123456789012")
    private UUID id;

    @Schema(description = "Идентификатор родительской секции", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
    private UUID eventSectionId;

    @Schema(description = "Уникальный идентификатор физического места на площадке", example = "d4e5f6a7-b8c9-0123-defg-234567890123")
    private UUID seatId;

    @Schema(description = "Текущий статус доступности места (FREE, RESERVED, SOLD)", example = "FREE")
    private EventSeatStatus status;

    @Schema(description = "Стоимость билета на это место", example = "1500.00")
    private BigDecimal price;

    @Schema(description = "Номер ряда", example = "5")
    private Integer rowNumber;

    @Schema(description = "Номер места в ряду", example = "12")
    private Integer seatNumber;

    @Schema(description = "Координата X центра или угла места на схеме", example = "150")
    private Integer x;

    @Schema(description = "Координата Y центра или угла места на схеме", example = "250")
    private Integer y;

    @Schema(description = "Ширина места на схеме", example = "40")
    private Integer width;

    @Schema(description = "Высота места на схеме", example = "40")
    private Integer height;

    @Schema(description = "Угол поворота места на схеме", example = "0")
    private Integer rotation;
}
