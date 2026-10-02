package com.bulka.eventservice.dto.response.event;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(
        name = "VenueSizeResponse",
        description = "Габариты площадки в условных единицах (используются для координатной сетки мест и секций)"
)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VenueSizeDto {
    @Schema(
            description = "Ширина площадки в условных единицах",
            example = "1000"
    )
    private Integer width;

    @Schema(
            description = "Высота площадки в условных единицах",
            example = "800"
    )
    private Integer height;
}
