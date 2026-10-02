package com.bulka.eventservice.dto.request.event;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

@Schema(
        name = "EventInfoPatch",
        description = """
        Частичное обновление основной информации о событии (PATCH).

        Семантика:
        - Поле отсутствует в запросе → значение НЕ меняется.
        - Поле присутствует → значение обновляется.
        - Пустой JSON `{}` → событие не меняется (no-op).

        Все поля опциональны. Минимум одно поле должно быть передано,
        иначе запрос будет отклонён с 400 Bad Request.
        """
)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventInfoRequestDto {
    @Schema(
            description = "Новое название события. Если не передано — остаётся прежним.",
            minLength = 1,
            maxLength = 255,
            nullable = true
    )
    private String name;

    @Schema(
            description = "Новое описание события. Если не передано — остаётся прежним.",
            nullable = true
    )
    private String description;

    @Schema(
            description = """
            Новая дата и время начала события (ISO-8601 с часовым поясом).
            Если не передано — остаётся прежним.
            Должна быть раньше endAt, если endAt также передан.
            """,
            example = "2026-05-15T10:00:00+03:00",
            nullable = true
    )
    private OffsetDateTime startAt;

    @Schema(
            description = """
            Новая дата и время окончания события (ISO-8601 с часовым поясом).
            Если не передано — остаётся прежним.
            Должна быть позже startAt, если startAt также передан.
            """,
            example = "2026-05-15T18:00:00+03:00",
            nullable = true
    )
    private OffsetDateTime endAt;
}
