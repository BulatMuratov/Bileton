package com.bulka.eventservice.kafka.publisher;

import com.bulka.eventservice.model.outbox.OutboxEvent;
import com.bulka.eventservice.model.outbox.OutboxEventStatus;
import com.bulka.eventservice.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Component
@RequiredArgsConstructor
public class OutboxPublisher {

    private static final String TOPIC = "events.lifecycle";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final OutboxEventRepository outboxEventRepository;

    @Scheduled(fixedDelay = 1000)
    @Transactional
    public void publishEvents(){
        List<OutboxEvent> outboxEventList = outboxEventRepository.findTop100ByStatusOrderByCreatedAtDesc(OutboxEventStatus.NEW);

        for(OutboxEvent outboxEvent : outboxEventList){
            publish(outboxEvent);
        }
    }

    private void publish(OutboxEvent outboxEvent){
        try {
            Message<String> message = MessageBuilder
                    .withPayload(outboxEvent.getPayload())
                    .setHeader(KafkaHeaders.TOPIC, TOPIC)
                    .setHeader(KafkaHeaders.KEY, outboxEvent.getAggregateId().toString())
                    .setHeader("messageId", outboxEvent.getId().toString())
                    .build();

            kafkaTemplate.send(message).get();

            outboxEvent.setStatus(OutboxEventStatus.PUBLISHED);
            outboxEvent.setPublishedAt(OffsetDateTime.now());

        } catch (InterruptedException ex){
            throw new RuntimeException("Thread interrupted while publishing event " + outboxEvent.getId(), ex);
        } catch (ExecutionException ex){
            throw new RuntimeException("Failed to publish event " + outboxEvent.getId(), ex);
        }
    }
}
