package com.bulka.apigateway.controller;

import com.bulka.apigateway.dto.event.EventDetailsResponse;
import com.bulka.apigateway.service.AvailabilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
public class AvailabilityController {

    private final AvailabilityService availabilityService;

    @GetMapping("/{eventId}/availability")
    public Mono<EventDetailsResponse> getAvailability(
            @PathVariable UUID eventId
    ) {
        return availabilityService.getAvailability(eventId);
    }
}
