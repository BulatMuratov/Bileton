package com.bulka.bookingservice.client.event;

import com.bulka.bookingservice.client.event.dto.EventSeatInfoDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "event-service")
public interface EventServiceClient {

    @GetMapping("/internal/events/{eventId}/seats")
    List<EventSeatInfoDto> getEventSeats(
            @PathVariable UUID eventId,
            @RequestParam List<UUID> seatIds);
}
