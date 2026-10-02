package com.bulka.bookingservice.controller;

import com.bulka.bookingservice.dto.ErrorResponse;
import com.bulka.bookingservice.dto.request.BookingRequestDto;
import com.bulka.bookingservice.dto.response.BookingDetailsResponseDto;

import com.bulka.bookingservice.dto.response.BookingInfoResponseDto;
import com.bulka.bookingservice.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@Tag(name = "Bookings", description = "Операции с бронированиями: создание, просмотр списка, получение деталей и отмена")
@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @Operation(summary = "Создать новое бронирование")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Бронирование успешно создано",
                    content = @Content(schema = @Schema(implementation = BookingDetailsResponseDto.class))),
            @ApiResponse(
                    responseCode = "400",
                    description = "Ошибка сериализации JSON, валидации или отсутствует Idempotency-Key",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(
                    responseCode = "401",
                    description = "Требуется аутентификация",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(
                    responseCode = "409",
                    description = "Конфликт: выбранные места уже забронированы",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<BookingDetailsResponseDto> createBooking(
            @RequestBody BookingRequestDto requestDto,
            Authentication authentication
    ) {
        UUID userId = UUID.fromString(authentication.getName());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(bookingService.createBooking(userId, requestDto));
    }

    @Operation(summary = "Получить список всех бронирований пользователя")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Список бронирований",
                    content = @Content(schema = @Schema(implementation = BookingInfoResponseDto.class))),
            @ApiResponse(
                    responseCode = "401",
                    description = "Требуется аутентификация",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping
    public ResponseEntity<List<BookingInfoResponseDto>> getBookingsForUser(
            Authentication authentication
    ) {
        UUID userId = UUID.fromString(authentication.getName());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(bookingService.getBookingsByUser(userId));
    }

    @Operation(summary = "Получить подробную информацию о бронировании")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Детальная информация о бронировании",
                    content = @Content(schema = @Schema(implementation = BookingDetailsResponseDto.class))),
            @ApiResponse(
                    responseCode = "400",
                    description = "Невалидный UUID бронирования",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(
                    responseCode = "401",
                    description = "Требуется аутентификация",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(
                    responseCode = "403",
                    description = "Доступ запрещен: бронирование принадлежит другому пользователю",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(
                    responseCode = "404",
                    description = "Бронирование не найдено",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingDetailsResponseDto> getBookingById(
            @PathVariable UUID bookingId,
            Authentication authentication
    ) {
        UUID userId = UUID.fromString(authentication.getName());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(bookingService.getBookingById(bookingId, userId));
    }

    @Operation(summary = "Отменить бронирование")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Бронирование успешно отменено",
                    content = @Content(schema = @Schema(implementation = BookingDetailsResponseDto.class))),
            @ApiResponse(
                    responseCode = "400",
                    description = "Невалидный UUID или ошибка состояния (нельзя отменить подтвержденное бронирование)",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(
                    responseCode = "401",
                    description = "Требуется аутентификация",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(
                    responseCode = "403",
                    description = "Доступ запрещен: бронирование принадлежит другому пользователю",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(
                    responseCode = "404",
                    description = "Бронирование или резерв не найдены",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping("/{bookingId}/cancel")
    public ResponseEntity<?> cancelBooking(
            @PathVariable UUID bookingId,
            Authentication authentication
    ) {
        UUID userId = UUID.fromString(authentication.getName());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(bookingService.cancelBooking(userId, bookingId));
    }



}
