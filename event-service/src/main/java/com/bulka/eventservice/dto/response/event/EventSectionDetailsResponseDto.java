package com.bulka.eventservice.dto.response.event;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Schema(
        name = "EventSectionDetailsResponse",
        description = """
        Детальная информация о секции события.
        Секция объединяет места по общему признаку (партер, VIP, балкон и т.д.)
        и содержит своё расположение на схеме зала.
        """
)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventSectionDetailsResponseDto {
    @Schema(
            description = "Уникальный идентификатор секции события (event section)",
            example = "7c9e6679-7425-40de-944b-e07fc1f90ae7"
    )
    private UUID id;

    @Schema(
            description = "Идентификатор события, к которому относится секция",
            example = "3fa85f64-5717-4562-b3fc-2c963f66afa6"
    )
    private UUID eventId;

    @Schema(
            description = "Идентификатор физической секции на площадке (venue section)",
            example = "c3d4e5f6-a7b8-9012-cdef-345678901234"
    )
    private UUID sectionId;

    @Schema(
            description = "Название секции",
            example = "VIP-зона"
    )
    private String name;

    @Schema(
            description = "Координата X левого верхнего угла секции на схеме зала",
            example = "200"
    )
    private Integer x;

    @Schema(
            description = "Координата Y левого верхнего угла секции на схеме зала",
            example = "150"
    )
    private Integer y;

    @Schema(
            description = "Ширина секции на схеме зала",
            example = "300"
    )
    private Integer width;

    @Schema(
            description = "Высота секции на схеме зала",
            example = "200"
    )
    private Integer height;

    @Schema(
            description = "Угол поворота секции на схеме зала в градусах (0, 90, 180, 270)",
            example = "0"
    )
    private Integer rotation;

    @ArraySchema(
            arraySchema = @Schema(description = "Список мест в секции"),
            schema = @Schema(implementation = EventSeatDetailsResponseDto.class)
    )
    private List<EventSeatDetailsResponseDto> seats;
}
