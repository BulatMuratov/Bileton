package com.bulka.eventservice.dto.response.event;

import com.bulka.eventservice.model.event.EventStatus;
import com.bulka.eventservice.model.event.EventType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;


@Schema(
        name = "EventSummary",
        description = """
        Компактная информация о событии для отображения в списках.
        Не содержит секций и мест — они доступны только в EventDetails.
        Используется в эндпоинтах постраничного поиска событий.
        """
)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventSummaryResponseDto {
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
}
