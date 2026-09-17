package com.bulka.paymentservice.kafka.outbox;

import com.bulka.paymentservice.model.OutboxEvent;
import com.bulka.paymentservice.model.OutboxEventStatus;
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
