package com.bulka.eventservice.dto.response.venue;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Schema(
        name = "SeatResponse",
        description = """
        Место на площадке.
        Содержит номер ряда, номер места и геометрию для отрисовки
        на интерактивной схеме зала.
        """
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeatResponseDto {
    @Schema(
            description = "Уникальный идентификатор места",
            example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890"
    )
    private UUID id;

    @Schema(
            description = "Номер ряда, в котором находится место",
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
            description = "Угол поворота места на схеме зала в градусах",
            example = "0",
            allowableValues = {"0", "90", "180", "270"}
    )
    private Integer rotation;
}