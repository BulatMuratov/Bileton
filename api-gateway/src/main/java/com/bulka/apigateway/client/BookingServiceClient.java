package com.bulka.apigateway.client;

import com.bulka.apigateway.dto.booking.ReservedSeatsResponse;
import com.bulka.apigateway.dto.event.EventDetailsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class BookingServiceClient {

    private final WebClient.Builder webClientBuilder;

    public Mono<ReservedSeatsResponse> getReservedSeats(UUID eventId){
        return webClientBuilder.build()
                .get()
                .uri("lb://booking-service/api/v1/internal/bookings/{eventId}/reserved-seats", eventId)
                .retrieve()
                .bodyToMono(ReservedSeatsResponse.class);
    }
}
