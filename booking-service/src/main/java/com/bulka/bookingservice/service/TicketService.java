package com.bulka.bookingservice.service;

import com.bulka.bookingservice.client.event.EventServiceClient;
import com.bulka.bookingservice.client.event.dto.TicketSnapshotResponse;
import com.bulka.bookingservice.dto.response.TicketDetailsResponse;
import com.bulka.bookingservice.dto.response.TicketSummaryResponse;
import com.bulka.bookingservice.exception.TicketNotFoundException;
import com.bulka.bookingservice.exception.booking.BookingNotFoundException;
import com.bulka.bookingservice.kafka.event.EventUpdatedEvent;
import com.bulka.bookingservice.mapper.TicketMapper;
import com.bulka.bookingservice.model.booking.Booking;
import com.bulka.bookingservice.model.booking.BookingItem;
import com.bulka.bookingservice.model.ticket.Ticket;
import com.bulka.bookingservice.model.ticket.TicketStatus;
import com.bulka.bookingservice.repository.BookingItemRepository;
import com.bulka.bookingservice.repository.BookingRepository;
import com.bulka.bookingservice.repository.ProcessedEventRepository;
import com.bulka.bookingservice.repository.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final BookingRepository bookingRepository;
    private final BookingItemRepository bookingItemRepository;
    private final ProcessedEventRepository processedEventRepository;

    private final EventServiceClient eventServiceClient;

    private final TicketMapper ticketMapper;

    @Transactional
    public void createTickets(UUID bookingId){
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(()-> new BookingNotFoundException("Booking not found"));

        List<BookingItem> bookingItems = bookingItemRepository.findAllByBookingId(bookingId);


        List<UUID> eventSeatIds = bookingItems.stream()
                .map(BookingItem::getEventSeatId)
                .toList();

        List<TicketSnapshotResponse> snapshots =
                eventServiceClient.getEventSeatsDetails(
                        booking.getEventId(),
                        eventSeatIds
                );
        snapshots.forEach(System.out::println);

        Map<UUID, TicketSnapshotResponse> snapshotsBySeatId =
                snapshots.stream()
                        .collect(Collectors.toMap(
                                TicketSnapshotResponse::getEventSeatId,
                                Function.identity()
                        ));

        List<Ticket> tickets = bookingItems.stream()
                .map(item -> createTicket(
                        booking,
                        item,
                        snapshotsBySeatId.get(item.getEventSeatId())
                ))
                .toList();

        ticketRepository.saveAll(tickets);

    }

    @Transactional(readOnly = true)
    public List<TicketSummaryResponse> getTicketsByUser(UUID userId){
        return ticketRepository.findAllByBookingUserId(userId)
                .stream()
                .map(ticketMapper::toSummaryResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public TicketDetailsResponse getTicket(UUID userId, UUID ticketId){
        Ticket ticket = ticketRepository.findByIdAndBookingUserId(ticketId, userId)
                .orElseThrow(() -> new TicketNotFoundException(
                        "Ticket with id " + ticketId + " not found"
                ));

        return ticketMapper.toDetailsResponse(ticket);
    }

    @Transactional
    public void handleUpdateEvent(EventUpdatedEvent event, UUID messageId) {
        int marked = processedEventRepository.markProcessed(messageId);
        if(marked == 0){
            return;
        }
        System.out.println("check2");
        ticketRepository.updateEventData(
                event.getEventId(),
                event.getName(),
                event.getStartAt(),
                event.getEndAt()
        );
    }

    private Ticket createTicket(
            Booking booking,
            BookingItem item,
            TicketSnapshotResponse snapshot
    ) {
        if (snapshot == null) {
            throw new IllegalStateException(
                    "Snapshot not found for event seat " + item.getEventSeatId()
            );
        }

        return Ticket.builder()
                .booking(booking)
                .bookingItem(item)
                .eventId(snapshot.getEventId())
                .ticketNumber(generateTicketNumber())
                .eventName(snapshot.getEventName())
                .eventStartAt(snapshot.getEventStartAt())
                .eventEndAt(snapshot.getEventEndAt())
                .venueName(snapshot.getVenueName())
                .sectionName(snapshot.getSectionName())
                .rowNumber(snapshot.getRowNumber())
                .seatNumber(snapshot.getSeatNumber())
                .price(item.getPrice())
                .currency("RUB")
                .status(TicketStatus.ACTIVE)
                .build();
    }

    private String generateTicketNumber() {
        return "TKT-" + UUID.randomUUID();
    }


}
