package com.bulka.bookingservice.mapper;

import com.bulka.bookingservice.dto.response.TicketDetailsResponse;
import com.bulka.bookingservice.dto.response.TicketSummaryResponse;
import com.bulka.bookingservice.model.Ticket;
import org.springframework.stereotype.Component;

@Component
public class TicketMapper {

    public TicketSummaryResponse toSummaryResponse(Ticket ticket) {
        return TicketSummaryResponse.builder()
                .id(ticket.getId())
                .ticketNumber(ticket.getTicketNumber())
                .eventName(ticket.getEventName())
                .eventStartAt(ticket.getEventStartAt())
                .eventEndAt(ticket.getEventEndAt())
                .venueName(ticket.getVenueName())
                .sectionName(ticket.getSectionName())
                .rowNumber(ticket.getRowNumber())
                .seatNumber(ticket.getSeatNumber())
                .price(ticket.getPrice())
                .status(ticket.getStatus())
                .build();
    }

    public TicketDetailsResponse toDetailsResponse(Ticket ticket) {
        return TicketDetailsResponse.builder()
                .id(ticket.getId())
                .ticketNumber(ticket.getTicketNumber())
                .bookingId(ticket.getBooking().getId())
                .eventId(ticket.getEventId())
                .eventName(ticket.getEventName())
                .eventStartAt(ticket.getEventStartAt())
                .eventEndAt(ticket.getEventEndAt())
                .venueName(ticket.getVenueName())
                .sectionName(ticket.getSectionName())
                .currency(ticket.getCurrency())
                .rowNumber(ticket.getRowNumber())
                .seatNumber(ticket.getSeatNumber())
                .price(ticket.getPrice())
                .status(ticket.getStatus())
                .createdAt(ticket.getCreatedAt())
                .build();
    }

}
