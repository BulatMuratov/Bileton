package com.bulka.eventservice.dto.request.venue;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Schema(
        name = "VenueDetailsRequest",
        description = """
        Запрос на создание.

        Содержит основную информацию о площадке (название, описание, габариты)
        и полный список секций с местами.
        """
)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VenueDetailsRequestDto {

    @Schema(
            description = "Название площадки",
            example = "Тандем",
            minLength = 1,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank
    private String name;

    @Schema(
            description = "Описание площадки",
            example = "Концертный зал на 6000 мест в Москве",
            minLength = 1,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotBlank
    private String description;

    @Schema(
            description = "Ширина площадки в условных единицах (используется для координатной сетки схемы зала)",
            example = "1000",
            minimum = "1",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull
    @Positive
    private Integer width;

    @Schema(
            description = "Высота площадки в условных единицах (используется для координатной сетки схемы зала)",
            example = "800",
            minimum = "1",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull
    @Positive
    private Integer height;

    @ArraySchema(
            arraySchema = @Schema(
                    description = "Список секций площадки. Должен содержать хотя бы одну секцию",
                    requiredMode = Schema.RequiredMode.REQUIRED
            ),
            schema = @Schema(implementation = SectionRequestDto.class)
    )
    @NotEmpty
    private List<@Valid SectionRequestDto> sections;
}
