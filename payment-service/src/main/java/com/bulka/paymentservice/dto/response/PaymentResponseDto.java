package com.bulka.paymentservice.dto.response;

import com.bulka.paymentservice.model.PaymentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Tag(
        name = "PaymentResponse",
        description = "Информация о платеже"
)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentResponseDto {
    @Schema(
            description = "Уникальный идентификатор платежа",
            example = "123e4567-e89b-12d3-a456-426614174000"
    )
    private UUID id;

    @Schema(
            description = "Уникальный идентификатор связанного бронирования",
            example = "123e4567-e89b-12d3-a456-426614174000"
    )
    private UUID bookingId;

    @Schema(
            description = "Сумма платежа",
            example = "1500.00"
    )
    private BigDecimal amount;

    @Schema(
            description = "Валюта платежа в формате ISO 4217",
            example = "RUB"
    )
    private String currency;

    @Schema(
            description = "Текущий статус платежа",
            example = "PENDING"
    )
    private PaymentStatus status;

    @Schema(
            description = "Наименование платежного провайдера",
            example = "YooKassa"
    )
    private String provider;

    @Schema(
            description = "Дата и время создания платежа в формате ISO 8601",
            example = "2023-10-25T14:30:00+03:00"
    )
    private OffsetDateTime createdAt;
}
