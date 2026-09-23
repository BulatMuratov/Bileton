package com.bulka.eventservice.kafka.outbox;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
public class EventSerializer {
    private final ObjectMapper objectMapper;

    public String serialize(Object event) {
        return objectMapper.writeValueAsString(event);
    }
}
