package com.bulka.eventservice.dto.response.venue;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Schema(
        name = "VenueSummaryResponse",
        description = """
        Компактная информация о площадке для отображения в списках.
        Не содержит секций и мест — они доступны только в VenueDetails.
        Используется в эндпоинтах постраничного поиска площадок
        и как вложенный объект в карточках событий.
        """
)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VenueSummaryResponseDto {
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
            description = "Ширина площадки в условных единицах",
            example = "1000"
    )
    private Integer width;

    @Schema(
            description = "Высота площадки в условных единицах",
            example = "800"
    )
    private Integer height;
}
