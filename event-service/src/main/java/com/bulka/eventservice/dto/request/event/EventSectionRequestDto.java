package com.bulka.eventservice.dto.request.event;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Schema(
        name = "EventSectionRequest",
        description = """
        Секция события (например, VIP-зона, партер, балкон).
        Ссылается на существующую секцию площадки по идентификатору
        и содержит список мест с ценами для этой секции.
        """
)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EventSectionRequestDto {
    @Schema(
            description = "Идентификатор существующей секции площадки (venue section)",
            example = "7c9e6679-7425-40de-944b-e07fc1f90ae7",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotNull
    private UUID id;

    @Schema(
            description = "Список мест в секции. Должен содержать хотя бы одно место",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    @NotEmpty
    private List<@Valid EventSeatRequestDto> seats;
}
