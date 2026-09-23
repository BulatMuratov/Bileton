package com.bulka.apigateway.service;

import com.bulka.apigateway.client.BookingServiceClient;
import com.bulka.apigateway.client.EventServiceClient;
import com.bulka.apigateway.dto.booking.ReservedSeatsResponse;
import com.bulka.apigateway.dto.event.EventDetailsResponse;
import com.bulka.apigateway.mapper.AvailabilityMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AvailabilityService {

    private final EventServiceClient eventServiceClient;
    private final BookingServiceClient bookingServiceClient;

    private final AvailabilityMapper availabilityMapper;

    public Mono<EventDetailsResponse> getAvailability(UUID eventId){
        Mono<EventDetailsResponse> event =
                eventServiceClient.getEvent(eventId);

        Mono<ReservedSeatsResponse> reservations =
                bookingServiceClient.getReservedSeats(eventId);

        return Mono.zip(event, reservations)
                .map(tuple -> availabilityMapper.merge(
                        tuple.getT1(),
                        tuple.getT2()
                ));
    }
}
