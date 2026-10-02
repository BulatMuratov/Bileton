package com.bulka.paymentservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(
        name = "ErrorResponse",
        description = """
        Универсальный формат ответа об ошибке для всех эндпоинтов API.
        Возвращается с Content-Type: application/json для всех ошибочных ответов.
        """
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    @Schema(
            description = "HTTP-статус ответа"
    )
    private int status;

    @Schema(
            description = "Краткое человекочитаемое название ошибки"
    )
    private String error;

    @Schema(
            description = """
            Машиночитаемый код ошибки для ветвления логики на клиенте.
            Рекомендуется использовать этот код, а не парсить message.
            """
    )
    private String code;

    @Schema(
            description = "Детальное описание конкретного случая"
    )
    private String message;
}