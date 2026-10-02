package com.bulka.eventservice.dto.request.venue;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Schema(
        name = "SectionRequest",
        description = """
        Секция площадки (партер, VIP-зона, балкон и т.д.).
        Содержит название, геометрию на схеме зала и список мест.
        Все поля обязательны, минимум одно место должно быть указано.
        """
)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SectionRequestDto {
    @Schema(
            description = "Название секции",
            example = "VIP-зона",
            minLength = 1,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank
    private String name;

    @Schema(
            description = "Координата X левого верхнего угла секции на схеме зала (в условных единицах)",
            example = "200",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull
    private Integer x;

    @Schema(
            description = "Координата Y левого верхнего угла секции на схеме зала (в условных единицах)",
            example = "150",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull
    private Integer y;

    @Schema(
            description = "Ширина секции на схеме зала (в условных единицах)",
            example = "300",
            minimum = "1",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull
    @Positive
    private Integer width;

    @Schema(
            description = "Высота секции на схеме зала (в условных единицах)",
            example = "200",
            minimum = "1",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull
    @Positive
    private Integer height;

    @Schema(
            description = "Угол поворота секции на схеме зала в градусах",
            example = "0",
            allowableValues = {"0", "90", "180", "270"},
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull
    private Integer rotation;

    @ArraySchema(
            arraySchema = @Schema(
                    description = "Список мест в секции. Должен содержать хотя бы одно место",
                    requiredMode = Schema.RequiredMode.REQUIRED
            ),
            schema = @Schema(implementation = SeatRequestDto.class)
    )
    @NotEmpty
    private List<@Valid SeatRequestDto> seats;
}
