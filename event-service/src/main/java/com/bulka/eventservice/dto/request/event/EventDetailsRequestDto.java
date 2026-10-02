package com.bulka.eventservice.dto.request.event;

import com.bulka.eventservice.model.event.EventType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Schema(
        name = "EventDetailsRequest",
        description = """
        Запрос на создание события.
        Содержит основную информацию о событии и полный список секций с местами.
        """
)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventDetailsRequestDto {
    @Schema(
            description = "Идентификатор площадки (venue), на которой проводится событие",
            example = "3fa85f64-5717-4562-b3fc-2c963f66afa6",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull
    private UUID venueId;

    @Schema(
            description = "Название события",
            minLength = 1,
            maxLength = 255,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank
    @Size(min = 1, max = 255)
    private String name;

    @Schema(
            description = "Описание события",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank
    @Size(min = 1, max = 255)
    private String description;

    @Schema(
            description = "Дата и время начала события (ISO-8601 с часовым поясом)",
            example = "2026-05-15T10:00:00+03:00",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull
    private OffsetDateTime startAt;

    @Schema(
            description = "Дата и время окончания события (ISO-8601 с часовым поясом)",
            example = "2026-05-15T18:00:00+03:00",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull
    private OffsetDateTime endAt;

    @Schema(
            description = "Тип события",
            example = "CONFERENCE",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull
    private EventType eventType;

    @Schema(
            description = "Список секций события. Должен содержать хотя бы одну секцию",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotEmpty
    private List<@Valid EventSectionRequestDto> sections;
}
