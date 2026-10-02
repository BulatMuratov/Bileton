package com.bulka.eventservice.controller;

import com.bulka.eventservice.dto.ErrorResponse;
import com.bulka.eventservice.dto.request.venue.VenueDetailsRequestDto;
import com.bulka.eventservice.dto.request.venue.VenueInfoRequestDto;
import com.bulka.eventservice.dto.response.venue.VenueDetailsResponseDto;
import com.bulka.eventservice.dto.response.venue.VenueSummaryResponseDto;
import com.bulka.eventservice.service.VenueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@Tag(name = "Venues", description = "Операции с площадками: создание, получение списка, получение полной информации и обновление")
@RestController
@RequestMapping("/api/v1/venues")
@RequiredArgsConstructor
public class VenueController {

    private final VenueService venueService;

    @Operation(summary = "Создать площадку")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Площадка успешно создана",
                    content = @Content(schema = @Schema(implementation = VenueDetailsResponseDto.class))),
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
                    description = "Недостаточно прав для создания площадки",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PostMapping
    public ResponseEntity<VenueDetailsResponseDto> createVenue(
            @RequestBody @Valid VenueDetailsRequestDto requestDto,
            @Parameter(required = true) @RequestHeader("Idempotency-Key") @NotBlank String idempotencyKey
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(venueService.createVenue(requestDto, idempotencyKey));
    }

    @Operation(
            summary = "Получить список всех площадок",
            description = "Возвращает краткую информацию обо всех доступных площадках"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Список площадок",
                    content = @Content(schema = @Schema(implementation = VenueSummaryResponseDto.class))),
            @ApiResponse(
                    responseCode = "401",
                    description = "Требуется аутентификация",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping
    public ResponseEntity<List<VenueSummaryResponseDto>> getAllVenues(){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(venueService.getAllVenuesSummary());
    }

    @Operation(summary = "Получить полную информацию о площадке")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Полная информация о площадке",
                    content = @Content(schema = @Schema(implementation = VenueDetailsResponseDto.class))),
            @ApiResponse(
                    responseCode = "400",
                    description = "Невалидный UUID площадки",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(
                    responseCode = "401",
                    description = "Требуется аутентификация",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(
                    responseCode = "404",
                    description = "Площадка не найдена",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @GetMapping("/{id}")
    public ResponseEntity<VenueDetailsResponseDto> getFullVenueInfo(
            @PathVariable UUID id
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(venueService.getVenueDetailsById(id));
    }

    @Operation(summary = "Частично обновить информацию о площадке")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Информация о площадке успешно обновлена",
                    content = @Content(schema = @Schema(implementation = VenueSummaryResponseDto.class))),
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
                    description = "Недостаточно прав для обновления площадки",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(
                    responseCode = "404",
                    description = "Площадка не найдена",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    @PatchMapping("/{id}")
    public ResponseEntity<VenueSummaryResponseDto> updateVenueInfo(
            @PathVariable UUID id,
            @RequestBody @Valid VenueInfoRequestDto request
    ){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(venueService.updateVenueInfo(id, request));

    }

//    @GetMapping("/{id}/seats")
//    public ResponseEntity<?> getSeatsOfVenue(@PathVariable UUID id) {
//        return ResponseEntity
//                .status(HttpStatus.OK)
//                .body(venueService.getSeatsByVenueId(id));
//    }

//    @PatchMapping("/{id}")
//    public ResponseEntity<?> updateVenue(@PathVariable UUID id, @RequestBody VenueInfoRequestDto requestDto) {
//        return ResponseEntity
//                .status(HttpStatus.OK)
//                .body(venueService.updateVenue(id, requestDto));
//    }

}
