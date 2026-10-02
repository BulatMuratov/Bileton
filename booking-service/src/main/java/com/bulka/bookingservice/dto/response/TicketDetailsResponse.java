package com.bulka.bookingservice.dto.response;

import com.bulka.bookingservice.model.ticket.TicketStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Schema(
        name = "TicketDetailsResponse",
        description = "Полная информация о билете"
)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TicketDetailsResponse {
    @Schema(
            description = "Уникальный идентификатор билета",
            example = "123e4567-e89b-12d3-a456-426614174000"
    )
    private UUID id;

    @Schema(
            description = "Уникальный номер билета для предъявления на входе",
            example = "TKT-20231025-00001"
    )
    private String ticketNumber;

    @Schema(
            description = "Уникальный идентификатор связанного бронирования",
            example = "123e4567-e89b-12d3-a456-426614174000"
    )
    private UUID bookingId;

    @Schema(
            description = "Уникальный идентификатор события",
            example = "123e4567-e89b-12d3-a456-426614174000"
    )
    private UUID eventId;

    @Schema(
            description = "Название события",
            example = "Концерт группы Example"
    )
    private String eventName;

    @Schema(
            description = "Дата и время начала события",
            example = "2023-11-15T19:00:00+03:00"
    )
    private OffsetDateTime eventStartAt;

    @Schema(
            description = "Дата и время окончания события",
            example = "2023-11-15T22:00:00+03:00"
    )
    private OffsetDateTime eventEndAt;

    @Schema(
            description = "Название площадки проведения события",
            example = "Ледовый дворец"
    )
    private String venueName;

    @Schema(
            description = "Название секции/зала",
            example = "Партер"
    )
    private String sectionName;

    @Schema(
            description = "Номер ряда",
            example = "5"
    )
    private Integer rowNumber;

    @Schema(
            description = "Номер места в ряду",
            example = "12"
    )
    private Integer seatNumber;

    @Schema(
            description = "Цена билета",
            example = "1500.00"
    )
    private BigDecimal price;

    @Schema(
            description = "Валюта цены билета в формате ISO 4217",
            example = "RUB"
    )
    private String currency;

    @Schema(
            description = "Текущий статус билета",
            example = "ACTIVE"
    )
    private TicketStatus status;

    @Schema(
            description = "Дата и время выпуска билета",
            example = "2023-10-25T14:35:00+03:00"
    )
    private OffsetDateTime createdAt;
}
