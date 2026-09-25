package com.bulka.bookingservice.kafka.outbox;

import com.bulka.bookingservice.model.outbox.OutboxEvent;
import com.bulka.bookingservice.model.outbox.OutboxEventStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OutboxEventFactory {

    private final EventSerializer eventSerializer;

    public OutboxEvent create(
            UUID eventId,
            String eventType,
            String aggregateType,
            UUID aggregateId,
            Object event) {
        return OutboxEvent.builder()
                .id(eventId)
                .eventType(eventType)
                .aggregateType(aggregateType)
                .aggregateId(aggregateId)
                .payload(eventSerializer.serialize(event))
                .status(OutboxEventStatus.NEW)
                .build();
    }
}