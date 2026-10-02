package com.bulka.paymentservice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Schema(
        name = "CreatePaymentRequest",
        description = "Запрос на создание платежа"
)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreatePaymentRequest {
    @Schema(
            description = "Уникальный идентификатор бронирования, для которого создается платеж",
            example = "123e4567-e89b-12d3-a456-426614174000"
    )
    private UUID bookingId;
}
