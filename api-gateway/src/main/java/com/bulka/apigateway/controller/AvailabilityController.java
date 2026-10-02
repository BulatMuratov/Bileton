package com.bulka.apigateway.controller;

import com.bulka.apigateway.dto.event.EventDetailsResponse;
import com.bulka.apigateway.service.AvailabilityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Tag(name = "Event Availability", description = "Агрегированные данные о доступности мест на событии")
@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
public class AvailabilityController {

    private final AvailabilityService availabilityService;

    @Operation(
            summary = "Получить актуальную доступность мест события",
            description = """
            Агрегирует информацию из нескольких микросервисов:
            1. Базовая информация о событии и структуре зала (из Event Service).
            2. Актуальный статус бронирования каждого места (из Booking Service).
            
            Возвращает полную структуру секций с указанием занятых и свободных мест.
            """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Успешное получение данных о доступности",
                    content = @Content(schema = @Schema(implementation = EventDetailsResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Невалидный UUID события",
                    content = @Content(schema = @Schema(implementation = com.bulka.apigateway.dto.ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Событие не найдено",
                    content = @Content(schema = @Schema(implementation = com.bulka.apigateway.dto.ErrorResponse.class))
            )
    })
    @GetMapping("/{eventId}/availability")
    public Mono<EventDetailsResponse> getAvailability(
            @PathVariable UUID eventId
    ) {
        return availabilityService.getAvailability(eventId);
    }
}
