package com.bulka.eventservice.dto.request.event;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Schema(
        name = "EventSeatRequest",
        description = """
        Место в секции события с указанием цены.
        Ссылается на существующее место площадки (venue seat) по идентификатору.
        """
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventSeatRequestDto {
    @Schema(
            description = "Идентификатор существующего места на площадке (venue seat)",
            example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull
    private UUID seatId;

    @Schema(
            description = "Цена места в указанной валюте события",
            example = "1500.00",
            minimum = "0.00",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull
    @DecimalMin(value = "0.00")
    private BigDecimal price;
}
