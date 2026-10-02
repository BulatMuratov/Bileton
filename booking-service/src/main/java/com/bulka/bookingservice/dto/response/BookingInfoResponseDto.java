package com.bulka.bookingservice.dto.response;

import com.bulka.bookingservice.model.booking.BookingStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Schema(
        name = "BookingInfoResponse",
        description = "Краткая информация о бронировании (без списка мест)"
)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookingInfoResponseDto {
    @Schema(
            description = "Уникальный идентификатор бронирования",
            example = "123e4567-e89b-12d3-a456-426614174000"
    )
    private UUID id;

    @Schema(
            description = "Уникальный идентификатор связанного события",
            example = "123e4567-e89b-12d3-a456-426614174000"
    )
    private UUID eventId;

    @Schema(
            description = "Текущий статус бронирования",
            example = "PENDING_PAYMENT"
    )
    private BookingStatus status;

    @Schema(
            description = "Общая стоимость бронирования",
            example = "3000.00"
    )
    private BigDecimal totalPrice;

    @Schema(
            description = "Дата и время создания бронирования в формате ISO 8601",
            example = "2023-10-25T14:30:00+03:00"
    )
    private OffsetDateTime createdAt;
}
