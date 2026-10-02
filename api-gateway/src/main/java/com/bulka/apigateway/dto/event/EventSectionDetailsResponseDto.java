package com.bulka.apigateway.dto.event;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Информация о секции зала с детализацией мест")
public class EventSectionDetailsResponseDto {
     @Schema(description = "Уникальный идентификатор связи секции с событием", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
    private UUID id;

    @Schema(description = "Уникальный идентификатор события", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID eventId;

    @Schema(description = "Уникальный идентификатор базовой секции площадки", example = "b2c3d4e5-f6a7-8901-bcde-f12345678901")
    private UUID sectionId;

    @Schema(description = "Название секции (например, 'Партер', 'Балкон')", example = "Партер")
    private String name;

    @Schema(description = "Координата X левого верхнего угла секции на схеме зала (в пикселях или условных единицах)", example = "100")
    private Integer x;

    @Schema(description = "Координата Y левого верхнего угла секции на схеме зала", example = "200")
    private Integer y;

    @Schema(description = "Ширина области секции на схеме", example = "500")
    private Integer width;

    @Schema(description = "Высота области секции на схеме", example = "300")
    private Integer height;

    @Schema(description = "Угол поворота секции на схеме (в градусах)", example = "0")
    private Integer rotation;

    @Schema(description = "Список всех мест в данной секции с их текущим статусом")
    private List<EventSeatDetailsResponseDto> seats;
}
