package com.bulka.eventservice.dto.request.event;

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
        name = "EventFilter",
        description = """
        Фильтр для поиска событий. Все поля опциональны.
        Если поле не передано — соответствующий критерий не применяется.
        Несколько переданных полей комбинируются по логике AND.
        """
)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventFilterRequest {
    @Schema(
            description = "Тип события для фильтрации",
            example = "CONFERENCE",
            nullable = true
    )
    private EventType eventType;
    @Schema(
            description = "Статус события для фильтрации",
            example = "PUBLISHED",
            nullable = true
    )
    private EventStatus status;
    @Schema(
            description = "Идентификатор площадки, на которой проходит событие",
            example = "3fa85f64-5717-4562-b3fc-2c963f66afa6",
            nullable = true
    )
    private UUID venueId;
    @Schema(
            description = "Нижняя граница периода: события, начинающиеся не раньше указанного момента (ISO-8601 с часовым поясом)",
            example = "2026-05-01T00:00:00+03:00",
            nullable = true
    )
    private OffsetDateTime from;
    @Schema(
            description = "Верхняя граница периода: события, заканчивающиеся не позже указанного момента (ISO-8601 с часовым поясом)",
            example = "2026-05-31T23:59:59+03:00",
            nullable = true
    )
    private OffsetDateTime to;
}
