package com.bulka.eventservice.controller;

import com.bulka.eventservice.dto.internal.request.EventSeatInfoDto;
import com.bulka.eventservice.service.EventInternalService;
import com.bulka.eventservice.service.EventService;
import jakarta.ws.rs.Path;
import lombok.RequiredArgsConstructor;
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

    @PostMapping("/{eventId}/seats/sell")
    public boolean sellSeats(@PathVariable UUID eventId, @RequestBody List<UUID> eventSeatIds) {
        return eventInternalService.sellSeats(eventId, eventSeatIds);
    }

    @GetMapping("/{eventId}/seats")
    public List<EventSeatInfoDto> getEventSeats(@PathVariable UUID eventId, @RequestParam List<UUID> eventSeatIds) {
        return eventInternalService.getEventSeats(eventId, eventSeatIds);
    }

//    @PostMapping("{eventId}/seat")
//    public ResponseEntity<?> reservations(@PathVariable UUID eventId){
//        return null;
//    }

//    @GetMapping("/{id}")
//    public ResponseEntity<?> reservation(@PathVariable UUID eventId){
//        return null;
//    }


}
