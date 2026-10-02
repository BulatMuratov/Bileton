package com.bulka.eventservice.dto.response.event;

import com.bulka.eventservice.model.event.EventStatus;
import com.bulka.eventservice.model.event.EventType;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Schema(
        name = "EventDetailsResponse",
        description = """
        Полная информация о событии, включая площадку, основную информацию
        и все секции с местами. Используется в эндпоинтах получения
        конкретного события по идентификатору.
        """
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventDetailsResponseDto {
    @Schema(
            description = "Уникальный идентификатор события",
            example = "3fa85f64-5717-4562-b3fc-2c963f66afa6"
    )
    private UUID id;

    @Schema(
            description = "Идентификатор площадки, на которой проводится событие",
            example = "b2c3d4e5-f6a7-8901-bcde-f23456789012"
    )
    private UUID venueId;

    @Schema(
            description = "Габариты площадки (ширина и высота в условных единицах)",
            implementation = VenueSizeDto.class
    )
    private VenueSizeDto venueSize;

    @Schema(
            description = "Название события",
            example = "Spring Boot Workshop 2026"
    )
    private String name;

    @Schema(
            description = "Описание события",
            example = "Практический воркшоп по Spring Boot 3 и микросервисам"
    )
    private String description;

    @Schema(
            description = "Дата и время начала события (ISO-8601 с часовым поясом)",
            example = "2026-05-15T10:00:00+03:00"
    )
    private OffsetDateTime startAt;

    @Schema(
            description = "Дата и время окончания события (ISO-8601 с часовым поясом)",
            example = "2026-05-15T18:00:00+03:00"
    )
    private OffsetDateTime endAt;

    @Schema(
            description = "Текущий статус события",
            example = "PUBLISHED"
    )
    private EventStatus status;

    @Schema(
            description = "Тип события",
            example = "CONFERENCE"
    )
    private EventType eventType;

    @Schema(
            description = "Дата и время создания записи о событии",
            example = "2026-04-01T12:30:00+03:00"
    )
    private OffsetDateTime createdAt;

    @Schema(
            description = "Дата и время последнего обновления события",
            example = "2026-04-10T09:15:00+03:00"
    )
    private OffsetDateTime updatedAt;

    @ArraySchema(
            arraySchema = @Schema(description = "Список секций события с местами"),
            schema = @Schema(implementation = EventSectionDetailsResponseDto.class)
    )
    private List<EventSectionDetailsResponseDto> sections;
}
