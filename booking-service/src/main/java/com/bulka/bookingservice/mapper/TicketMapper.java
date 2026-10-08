package com.bulka.bookingservice.mapper;

import com.bulka.bookingservice.dto.projection.TicketDetailsProjection;
import com.bulka.bookingservice.dto.projection.TicketSummaryProjection;
import com.bulka.bookingservice.dto.response.TicketDetailsResponse;
import com.bulka.bookingservice.dto.response.TicketSummaryResponse;
import com.bulka.bookingservice.model.ticket.Ticket;
import org.springframework.stereotype.Component;

@Component
public class TicketMapper {

    public TicketSummaryResponse toSummaryResponseFromProjection(TicketSummaryProjection ticket) {
        return TicketSummaryResponse.builder()
                .id(ticket.id())
                .ticketNumber(ticket.ticketNumber())
                .eventName(ticket.eventName())
                .eventStartAt(ticket.eventStartAt())
                .eventEndAt(ticket.eventEndAt())
                .venueName(ticket.venueName())
                .sectionName(ticket.sectionName())
                .rowNumber(ticket.rowNumber())
                .seatNumber(ticket.seatNumber())
                .price(ticket.price())
                .status(ticket.status())
                .build();
    }

    public TicketDetailsResponse toDetailsResponseFromProjection(TicketDetailsProjection ticket) {
        return TicketDetailsResponse.builder()
                .id(ticket.id())
                .ticketNumber(ticket.ticketNumber())
                .bookingId(ticket.bookingId())
                .eventId(ticket.eventId())
                .eventName(ticket.eventName())
                .eventStartAt(ticket.eventStartAt())
                .eventEndAt(ticket.eventEndAt())
                .venueName(ticket.venueName())
                .sectionName(ticket.sectionName())
                .currency(ticket.currency())
                .rowNumber(ticket.rowNumber())
                .seatNumber(ticket.seatNumber())
                .price(ticket.price())
                .status(ticket.status())
                .createdAt(ticket.createdAt())
                .build();
    }

}
