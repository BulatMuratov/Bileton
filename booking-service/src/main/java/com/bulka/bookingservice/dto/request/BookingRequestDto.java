package com.bulka.bookingservice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Schema(description = "Запрос на создание бронирования")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookingRequestDto {
    @Schema(
            description = "Уникальный идентификатор события, на которое создается бронирование",
            example = "123e4567-e89b-12d3-a456-426614174000"
    )
    private UUID eventId;

    @Schema(
            description = "Список уникальных идентификаторов мест на событии, которые необходимо забронировать",
            example = "[\"a1b2c3d4-e5f6-7890-abcd-ef1234567890\", \"b2c3d4e5-f6a7-8901-bcde-f12345678901\"]"
    )
    private List<UUID> eventSeatIds;
}
