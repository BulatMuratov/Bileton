package com.bulka.eventservice.dto.response.venue;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Schema(
        name = "SectionResponse",
        description = """
        Секция площадки (партер, VIP-зона, балкон и т.д.).
        Содержит название, геометрию на схеме зала и список мест.
        """
)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SectionResponseDto {
    @Schema(
            description = "Уникальный идентификатор секции",
            example = "7c9e6679-7425-40de-944b-e07fc1f90ae7"
    )
    private UUID id;

    @Schema(
            description = "Название секции",
            example = "VIP-зона"
    )
    private String name;

    @Schema(
            description = "Координата X левого верхнего угла секции на схеме зала (в условных единицах)",
            example = "200"
    )
    private Integer x;

    @Schema(
            description = "Координата Y левого верхнего угла секции на схеме зала (в условных единицах)",
            example = "150"
    )
    private Integer y;

    @Schema(
            description = "Ширина секции на схеме зала (в условных единицах)",
            example = "300"
    )
    private Integer width;

    @Schema(
            description = "Высота секции на схеме зала (в условных единицах)",
            example = "200"
    )
    private Integer height;

    @Schema(
            description = "Угол поворота секции на схеме зала в градусах",
            example = "0",
            allowableValues = {"0", "90", "180", "270"}
    )
    private Integer rotation;

    @ArraySchema(
            arraySchema = @Schema(description = "Список мест в секции"),
            schema = @Schema(implementation = SeatResponseDto.class)
    )
    private List<SeatResponseDto> seats;
}