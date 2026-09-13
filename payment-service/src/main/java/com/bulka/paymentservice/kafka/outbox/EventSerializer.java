package com.bulka.paymentservice.kafka.outbox;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
public class EventSerializer {
    private final ObjectMapper objectMapper;

    public String serialize(Object event) {
//        try{
            return objectMapper.writeValueAsString(event);
//        } catch (JsonProcessingException e) {
//            System.out.println(e.getMessage());
//            throw e;
//        }
//            throw new RuntimeException("Failed to serialize event", e);
//            throw new EventSerializationException("Failed to serialize event", e);
    }
}
