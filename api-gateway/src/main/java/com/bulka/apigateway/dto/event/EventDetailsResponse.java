package com.bulka.apigateway.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Агрегированная информация о событии с актуальной доступностью мест")
public class EventDetailsResponse {
    @Schema(description = "Уникальный идентификатор события", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID id;

    @Schema(description = "Уникальный идентификатор площадки", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID venueId;

    @Schema(description = "Информация о размере и вместимости площадки")
    private VenueSizeDto venueSize;

    @Schema(description = "Название события", example = "Рок-концерт 'Легенды'")
    private String name;

    @Schema(description = "Описание события")
    private String description;

    @Schema(description = "Дата и время начала события", example = "2023-11-15T19:00:00+03:00")
    private OffsetDateTime startAt;

    @Schema(description = "Дата и время окончания события", example = "2023-11-15T22:00:00+03:00")
    private OffsetDateTime endAt;

    @Schema(description = "Текущий статус события", example = "PUBLISHED")
    private EventStatus status;

    @Schema(description = "Дата создания записи")
    private OffsetDateTime createdAt;

    @Schema(description = "Дата последнего обновления")
    private OffsetDateTime updatedAt;

    @Schema(description = "Список секций зала с информацией о доступности мест в каждой")
    private List<EventSectionDetailsResponseDto> sections;
}
