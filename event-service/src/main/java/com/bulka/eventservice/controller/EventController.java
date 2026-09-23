package com.bulka.eventservice.controller;

import com.bulka.eventservice.dto.request.event.EventDetailsRequestDto;
import com.bulka.eventservice.dto.request.event.EventInfoRequestDto;
import com.bulka.eventservice.dto.response.event.EventDetailsResponseDto;
import com.bulka.eventservice.dto.response.event.EventSummaryResponseDto;
import com.bulka.eventservice.service.EventService;
import jakarta.validation.Valid;
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

@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @PostMapping
    public ResponseEntity<EventDetailsResponseDto> createEvent(
            @Valid @RequestBody EventDetailsRequestDto requestDto,
            @RequestHeader("Idempotency-Key") String idempotencyKey
    ) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(eventService.createEvent(requestDto, idempotencyKey));
    }

    @GetMapping
    public ResponseEntity<List<EventSummaryResponseDto>> getAllEvents(){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(eventService.getEvents());
    }

    @PatchMapping("/{eventId}")
    public ResponseEntity<EventSummaryResponseDto> updateEvent(
            @PathVariable UUID eventId,
            @RequestBody EventInfoRequestDto requestDto) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(eventService.updateEvent(eventId, requestDto));
    }

    @PostMapping("/{eventId}/publish")
    public ResponseEntity<EventSummaryResponseDto> publishEvent(
            @PathVariable UUID eventId
    ) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(eventService.publishEvent(eventId));
    }

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

//    @GetMapping
//    public ResponseEntity<Page<EventInfoResponseDto>> getEventsByFilters(@ModelAttribute EventFilterRequest filterRequest, Pageable pageable){
//        return ResponseEntity
//                .status(HttpStatus.OK)
//                .body(eventService.getEvents(filterRequest, pageable));
//    }

}
