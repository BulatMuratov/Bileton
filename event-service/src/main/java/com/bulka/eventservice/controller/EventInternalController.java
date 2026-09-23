package com.bulka.eventservice.controller;

import com.bulka.eventservice.dto.internal.response.EventSeatDetailsInfoDto;
import com.bulka.eventservice.dto.internal.response.EventSeatInfoDto;
import com.bulka.eventservice.dto.response.event.EventDetailsResponseDto;
import com.bulka.eventservice.service.EventInternalService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/internal/events/")
@RequiredArgsConstructor
public class EventInternalController {

    private final EventInternalService eventInternalService;

    @GetMapping("/{eventId}")
    public ResponseEntity<EventDetailsResponseDto> getEventById(
            @PathVariable UUID eventId
    ){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(eventInternalService.getEventById(eventId));
    }

    @PostMapping("/{eventId}/seats/sell")
    public boolean sellSeats(
            @PathVariable UUID eventId,
            @RequestBody List<UUID> eventSeatIds
    ) {
        return eventInternalService.sellSeats(eventId, eventSeatIds);
    }

    @GetMapping("/{eventId}/seats")
    public List<EventSeatInfoDto> getEventSeats(
            @PathVariable UUID eventId,
            @RequestParam @Valid List<@NotNull UUID> eventSeatIds
    ) {
        return eventInternalService.getEventSeats(eventId, eventSeatIds);
    }

    @GetMapping("/{eventId}/seats-details")
    public List<EventSeatDetailsInfoDto> getEventSeatsDetails(
            @PathVariable UUID eventId,
            @RequestParam @Valid List<@NotNull UUID> eventSeatIds
    ) {
        return eventInternalService.getEventSeatsDetails(eventId, eventSeatIds);
    }
}
