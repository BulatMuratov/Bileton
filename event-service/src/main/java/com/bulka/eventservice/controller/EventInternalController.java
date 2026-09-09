package com.bulka.eventservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("api/v1/internal/events/")
public class EventInternalController {
    @PostMapping("{eventId}/reservations")
    public ResponseEntity<?> reservations(@PathVariable UUID eventId){
        return null;
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> reservation(@PathVariable UUID eventId){
        return null;
    }


}
