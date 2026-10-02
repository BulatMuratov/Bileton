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
        name = "VenueDetailsResponse",
        description = """
        Полная информация о площадке, включая все секции и места.
        Используется в эндпоинтах получения конкретной площадки по идентификатору.
        """
)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VenueDetailsResponseDto {
    @Schema(
            description = "Уникальный идентификатор площадки",
            example = "b2c3d4e5-f6a7-8901-bcde-f23456789012"
    )
    private UUID id;

    @Schema(
            description = "Название площадки",
            example = "Крокус Сити Холл"
    )
    private String name;

    @Schema(
            description = "Описание площадки",
            example = "Концертный зал на 6000 мест в Москве"
    )
    private String description;

    @Schema(
            description = "Ширина площадки в условных единицах (используется для координатной сетки схемы зала)",
            example = "1000"
    )
    private Integer width;

    @Schema(
            description = "Высота площадки в условных единицах (используется для координатной сетки схемы зала)",
            example = "800"
    )
    private Integer height;

    @ArraySchema(
            arraySchema = @Schema(description = "Список секций площадки с местами"),
            schema = @Schema(implementation = SectionResponseDto.class)
    )
    private List<SectionResponseDto> sections;
}
