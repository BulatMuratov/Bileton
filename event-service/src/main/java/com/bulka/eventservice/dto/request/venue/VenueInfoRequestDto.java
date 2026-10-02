package com.bulka.eventservice.dto.request.venue;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(
        name = "VenueInfoPatch",
        description = """
        Частичное обновление основной информации о площадке (PATCH).

        Семантика:
        - Поле отсутствует в запросе → значение НЕ меняется.
        - Поле присутствует → значение обновляется.
        - Пустой JSON `{}` → площадка не меняется (no-op).

        Все поля опциональны. Минимум одно поле должно быть передано,
        иначе запрос будет отклонён с 400 Bad Request.
        """
)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VenueInfoRequestDto {
    @Schema(
            description = "Новое название площадки. Если не передано — остаётся прежним.",
            example = "Тандем (renamed)",
            minLength = 1,
            maxLength = 255,
            nullable = true
    )
    @Size(min = 1, max = 255)
    private String name;

    @Schema(
            description = "Новое описание площадки. Если не передано — остаётся прежним.",
            example = "Обновлённое описание концертного зала",
            minLength = 1,
            maxLength = 1000,
            nullable = true
    )
    @NotBlank
    private String description;
}
