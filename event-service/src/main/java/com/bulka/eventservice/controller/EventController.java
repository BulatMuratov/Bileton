package com.bulka.eventservice.controller;

import com.bulka.eventservice.dto.request.EventDetailsRequestDto;
import com.bulka.eventservice.dto.request.EventInfoRequestDto;
import com.bulka.eventservice.dto.response.EventDetailsResponseDto;
import com.bulka.eventservice.dto.response.EventInfoResponseDto;
import com.bulka.eventservice.dto.response.EventSeatsFullInfoResponseDto;
import com.bulka.eventservice.service.EventService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/events")
@AllArgsConstructor
public class EventController {

    private final EventService eventService;

    @PostMapping
    public ResponseEntity<EventDetailsResponseDto> createEvent(@RequestBody EventDetailsRequestDto requestDto) {
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(eventService.createEvent(requestDto));
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<EventInfoResponseDto> getEventById(@PathVariable UUID eventId){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(eventService.getEventById(eventId));
    }

    @GetMapping("/{eventId}/seats")
    public ResponseEntity<EventSeatsFullInfoResponseDto> getEventSeatsByEventId(@PathVariable UUID eventId){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(eventService.getEventSeatsByEventId(eventId));
    }

    @GetMapping
    public ResponseEntity<List<EventInfoResponseDto>> getAllEvents(){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(eventService.getAllEvents());
    }

    @PatchMapping("/{eventId}")
    public ResponseEntity<EventInfoResponseDto> updateEvent(@PathVariable UUID eventId,@RequestBody EventInfoRequestDto requestDto) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(eventService.updateEvent(eventId, requestDto));
    }

//    @GetMapping
//    public ResponseEntity<?> getEventsByFilters

    @PostMapping("/{eventId}/publish")
    public ResponseEntity<EventInfoResponseDto> publishEvent(@PathVariable UUID eventId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(eventService.publishEvent(eventId));
    }

    @PostMapping("/{eventId}/cancel")
    public ResponseEntity<EventInfoResponseDto> cancelEvent(@PathVariable UUID eventId){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(eventService.cancelEvent(eventId));
    }

//    @DeleteMapping("/{eventId}")
//    @ResponseStatus(HttpStatus.NO_CONTENT)
//    public void deleteEvent(@PathVariable UUID eventId){
//        eventService.deleteEvent(eventId);
//    }
}
