package com.bulka.apigateway.mapper;

import com.bulka.apigateway.dto.booking.ReservedSeatsResponse;
import com.bulka.apigateway.dto.event.EventDetailsResponse;
import com.bulka.apigateway.dto.event.EventSeatDetailsResponseDto;
import com.bulka.apigateway.dto.event.EventSeatStatus;
import com.bulka.apigateway.dto.event.EventSectionDetailsResponseDto;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.UUID;

@Component
public class AvailabilityMapper {

    public EventDetailsResponse merge(
            EventDetailsResponse event,
            ReservedSeatsResponse reservations
    ) {
        Set<UUID> reservedSeatIds = Set.copyOf(
                reservations.getReservedEventSeatsId()
        );

        for (EventSectionDetailsResponseDto section : event.getSections()) {
            for (EventSeatDetailsResponseDto seat : section.getSeats()) {

                if (reservedSeatIds.contains(seat.getId())) {
                    seat.setStatus(EventSeatStatus.SOLD);
                }
            }
        }
        return event;
    }
}
