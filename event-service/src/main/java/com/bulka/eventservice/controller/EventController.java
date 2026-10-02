package com.bulka.eventservice.controller;

import com.bulka.eventservice.dto.ErrorResponse;
import com.bulka.eventservice.dto.request.event.EventDetailsRequestDto;
import com.bulka.eventservice.dto.request.event.EventFilterRequest;
import com.bulka.eventservice.dto.request.event.EventInfoRequestDto;
import com.bulka.eventservice.dto.response.event.EventDetailsResponseDto;
import com.bulka.eventservice.dto.response.event.EventSummaryResponseDto;
import com.bulka.eventservice.model.event.EventType;
import com.bulka.eventservice.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;


@Tag(name = "Events", description = "Операции с событиями: создание, поиск, обновление, публикация и отмена")
@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @Operation(summary = "Создать событие")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Событие успешно создано",
                    content = @Content(schema = @Schema(implementation = EventDetailsResponseDto.class))),
            @ApiResponse(
                    responseCode = "400",
                    description = "Ошибка сериализации JSON, валидации или отсутствует Idempotency-Key",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(
                    responseCode = "401",
                    description = "Требуется аутентификация",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(
                    responseCode = "403",
                    description = "Недостаточно прав для создания события",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(
                    responseCode = "404",
                    description = "Указанная площадка, секция или место не найдены",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(
                    responseCode = "409",
                    description = "Конфликт: дубликат секции или места",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<EventDetailsResponseDto> createEvent(
            @Valid @RequestBody EventDetailsRequestDto requestDto,
            @Parameter(required = true) @RequestHeader("Idempotency-Key") String idempotencyKey
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(eventService.createEvent(requestDto, idempotencyKey));
    }

    @Operation(
            summary = "Найти события по фильтру",
            description = """
            Возвращает список событий, отфильтрованных по переданным критериям.
            Все критерии опциональны. Пагинация через стандартные query-параметры page, size, sort.
            """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Список событий",
                    content = @Content(schema = @Schema(implementation = EventSummaryResponseDto.class))),
            @ApiResponse(
                    responseCode = "400",
                    description = "Невалидный формат query-параметра",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(
                    responseCode = "401",
                    description = "Требуется аутентификация",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
    })
    @GetMapping
    public ResponseEntity<List<EventSummaryResponseDto>> getEventsByFilter(
            @ModelAttribute EventFilterRequest filterRequest, Pageable pageable
    ){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(eventService.getEvents(filterRequest, pageable));
    }

    @Operation(
            summary = "Получить список типов событий",
            description = "Возвращает все возможные значения enum EventType"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Список типов событий",
                    content = @Content(schema = @Schema(implementation = EventType.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Требуется аутентификация",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Недостаточно прав",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/types")
    public ResponseEntity<List<EventType>> getAllEventTypes(){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(Arrays.asList(EventType.values()));
    }

    @Operation(summary = "Частично обновить событие")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Событие успешно обновлено",
                    content = @Content(schema = @Schema(implementation = EventSummaryResponseDto.class))),
            @ApiResponse(
                    responseCode = "400",
                    description = "Невалидный UUID, ошибка сериализации, валидации или пустое тело",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(
                    responseCode = "401",
                    description = "Требуется аутентификация",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(
                    responseCode = "403",
                    description = "Недостаточно прав для обновления события",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(
                    responseCode = "404",
                    description = "Событие не найдено",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PatchMapping("/{eventId}")
    public ResponseEntity<EventSummaryResponseDto> updateEvent(
            @PathVariable UUID eventId,
            @RequestBody EventInfoRequestDto requestDto) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(eventService.updateEvent(eventId, requestDto));
    }

    @Operation(summary = "Опубликовать событие")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Событие опубликовано",
                    content = @Content(schema = @Schema(implementation = EventSummaryResponseDto.class))),
            @ApiResponse(
                    responseCode = "400",
                    description = "Невалидный UUID события",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(
                    responseCode = "401",
                    description = "Требуется аутентификация",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(
                    responseCode = "403",
                    description = "Недостаточно прав для публикации события",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(
                    responseCode = "404",
                    description = "Событие не найдено",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(
                    responseCode = "409",
                    description = "Событие нельзя опубликовать из текущего статуса",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/{eventId}/publish")
    public ResponseEntity<EventSummaryResponseDto> publishEvent(
            @PathVariable UUID eventId
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(eventService.publishEvent(eventId));
    }


    @Operation(summary = "Отменить событие")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Событие отменено",
                    content = @Content(schema = @Schema(implementation = EventSummaryResponseDto.class))),
            @ApiResponse(
                    responseCode = "400",
                    description = "Невалидный UUID события",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(
                    responseCode = "401",
                    description = "Требуется аутентификация",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(
                    responseCode = "403",
                    description = "Недостаточно прав для отмены события",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(
                    responseCode = "404",
                    description = "Событие не найдено",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(
                    responseCode = "409",
                    description = "Событие нельзя отменить из текущего статуса",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/{eventId}/cancel")
    public ResponseEntity<EventSummaryResponseDto> cancelEvent(
            @PathVariable UUID eventId
    ){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(eventService.cancelEvent(eventId));
    }

//    @GetMapping("/{eventId}/seats")
//    public ResponseEntity<EventSeatsFullInfoResponseDto> getEventSeatsByEventId(@PathVariable UUID eventId){
//        return ResponseEntity
//                .status(HttpStatus.OK)
//                .body(eventService.getEventSeatsByEventId(eventId));
//    }

}
