package com.bulka.apigateway.client;

import com.bulka.apigateway.dto.event.EventDetailsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EventServiceClient {

    private final WebClient.Builder webClientBuilder;

    public Mono<EventDetailsResponse> getEvent(UUID eventId) {
        return webClientBuilder.build()
                .get()
                .uri("lb://event-service/api/v1/internal/events/{eventId}", eventId)
                .retrieve()
                .bodyToMono(EventDetailsResponse.class);
    }
}