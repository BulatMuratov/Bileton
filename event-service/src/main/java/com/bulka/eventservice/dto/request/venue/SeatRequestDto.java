package com.bulka.eventservice.dto.request.venue;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;


@Schema(
        name = "SeatRequest",
        description = """
        Место на площадке. Содержит номер ряда, номер места и геометрию
        для отрисовки на интерактивной схеме зала.
        Все поля обязательны.
        """
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeatRequestDto {

    @Schema(
            description = "Номер ряда, в котором находится место",
            example = "5",
            minimum = "1",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull
    @Positive
    private Integer rowNumber;

    @Schema(
            description = "Номер места в ряду",
            example = "12",
            minimum = "1",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull
    @Positive
    private Integer seatNumber;

    @Schema(
            description = "Координата X левого верхнего угла места на схеме зала (в условных единицах)",
            example = "120",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull
    private Integer x;

    @Schema(
            description = "Координата Y левого верхнего угла места на схеме зала (в условных единицах)",
            example = "340",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull
    private Integer y;

    @Schema(
            description = "Ширина места на схеме зала (в условных единицах)",
            example = "20",
            minimum = "1",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull
    @Positive
    private Integer width;

    @Schema(
            description = "Высота места на схеме зала (в условных единицах)",
            example = "20",
            minimum = "1",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull
    @Positive
    private Integer height;

    @Schema(
            description = "Угол поворота места на схеме зала в градусах",
            example = "0",
            allowableValues = {"0", "90", "180", "270"},
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull
    private Integer rotation;
}
