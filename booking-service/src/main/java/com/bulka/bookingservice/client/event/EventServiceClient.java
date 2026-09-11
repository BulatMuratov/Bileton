package com.bulka.bookingservice.client.event;

import com.bulka.bookingservice.client.event.dto.EventSeatInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "event-service")
public interface EventServiceClient {

    @GetMapping("/api/v1/internal/events/{eventId}/seats")
    List<EventSeatInfo> getEventSeats(
            @PathVariable("eventId") UUID eventId,
            @RequestParam("eventSeatIds") List<UUID> eventSeatIds
    );
}
