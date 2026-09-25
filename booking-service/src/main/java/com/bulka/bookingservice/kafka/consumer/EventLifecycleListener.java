package com.bulka.bookingservice.kafka.consumer;

import com.bulka.bookingservice.kafka.event.EventUpdatedEvent;
import com.bulka.bookingservice.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EventLifecycleListener {

    private final TicketService ticketService;

    @KafkaListener(
            topics = "events.lifecycle",
            groupId = "booking-service",
            containerFactory = "eventKafkaListenerContainerFactory"
    )
    public void handle(EventUpdatedEvent event, @Header("messageId") UUID messageId) {
        System.out.println("check1");
        ticketService.handleUpdateEvent(event, messageId);
    }
}