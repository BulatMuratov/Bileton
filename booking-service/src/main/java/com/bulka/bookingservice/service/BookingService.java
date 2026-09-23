package com.bulka.bookingservice.service;

import com.bulka.bookingservice.client.event.EventServiceClient;
import com.bulka.bookingservice.client.event.dto.EventSeatInfo;
import com.bulka.bookingservice.client.event.dto.EventSeatStatus;
import com.bulka.bookingservice.dto.request.BookingRequestDto;
import com.bulka.bookingservice.dto.response.BookingDetailsResponseDto;
import com.bulka.bookingservice.dto.response.BookingInfoResponseDto;
import com.bulka.bookingservice.dto.response.EventSeatResponseDto;
import com.bulka.bookingservice.exception.ForbiddenException;
import com.bulka.bookingservice.exception.booking.BookingNotFoundException;
import com.bulka.bookingservice.exception.booking.EventSeatMismatchException;
import com.bulka.bookingservice.exception.booking.IllegalStateException;
import com.bulka.bookingservice.exception.booking.ReservationNotFoundException;
import com.bulka.bookingservice.exception.booking.SeatValidationException;
import com.bulka.bookingservice.exception.booking.SeatsAlreadyReservedException;
import com.bulka.bookingservice.kafka.dto.PaymentSucceededEvent;
import com.bulka.bookingservice.model.Booking;
import com.bulka.bookingservice.model.BookingItem;
import com.bulka.bookingservice.model.BookingStatus;
import com.bulka.bookingservice.repository.BookingItemRepository;
import com.bulka.bookingservice.repository.BookingRepository;
import com.bulka.bookingservice.repository.ProcessedEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final RedisService redisService;
    private final TicketService ticketService;

//    @Value("${redis.ttl-minute}")
    private final Duration reservedTtl =  Duration.ofMinutes(10);

    private final BookingRepository bookingRepository;
    private final BookingItemRepository bookingItemRepository;
    private final ProcessedEventRepository processedEventRepository;

    private final EventServiceClient eventServiceClient;

    @Transactional
    public BookingDetailsResponseDto createBooking(UUID userId, BookingRequestDto request){
        UUID bookingId = UUID.randomUUID();

        List<EventSeatInfo> eventSeats = eventServiceClient.getEventSeats(
                request.getEventId(),
                request.getEventSeatIds()
        );

        validateEventSeats(request, eventSeats);

        BigDecimal totalPrice = eventSeats.stream()
                .map(EventSeatInfo::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        OffsetDateTime expiresAt = OffsetDateTime.now().plus(reservedTtl);

        boolean reserved = redisService.reserve(bookingId, request.getEventId(), request.getEventSeatIds());
        if (!reserved) {
            throw new SeatsAlreadyReservedException("Reservation failed");
        }

        try {
            Booking savedBooking = bookingRepository.save(Booking.builder()
                    .id(bookingId)
                    .userId(userId)
                    .eventId(request.getEventId())
                    .status(BookingStatus.PENDING)
                    .expiresAt(expiresAt)
                    .totalPrice(totalPrice)
                    .build());

            List<BookingItem> bookingItems = eventSeats.stream()
                    .map(seat -> BookingItem.builder()
                            .booking(savedBooking)
                            .eventSeatId(seat.getEventSeatId())
                            .price(seat.getPrice())
                            .build())
                    .toList();

            List<BookingItem> savedBookingItems = bookingItemRepository.saveAll(bookingItems);

            return BookingDetailsResponseDto.builder()
                    .id(savedBooking.getId())
                    .eventId(savedBooking.getEventId())
                    .status(savedBooking.getStatus())
                    .totalPrice(totalPrice)
                    .createdAt(savedBooking.getCreatedAt())
                    .items(savedBookingItems.stream()
                            .map(bookingItem -> EventSeatResponseDto.builder()
                                    .eventSeatId(bookingItem.getEventSeatId())
                                    .price(bookingItem.getPrice())
                                    .build())
                            .toList())
                    .build();
        }
        catch (RuntimeException e) {
            try {
                redisService.release(
                        bookingId,
                        request.getEventId(),
                        request.getEventSeatIds()
                );
            } catch (Exception releaseException) {
                e.addSuppressed(releaseException);
            }
            throw e;
        }
    }

    public List<BookingInfoResponseDto> getBookingsByUser(UUID userId){
        List<Booking> bookingList = bookingRepository.findAllByUserId(userId);

        return bookingList.stream()
                .map(this::toInfoResponse)
                .toList();
    }

    public BookingDetailsResponseDto getBookingById(UUID bookingId, UUID userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Booking with id " + bookingId + " not found"));

        if(!booking.getUserId().equals(userId)){
            throw new ForbiddenException("Access denied: this booking belongs to another user");
        }

        List<BookingItem> bookingItems = bookingItemRepository.findAllByBookingId(bookingId);

        return BookingDetailsResponseDto.builder()
                .id(booking.getId())
                .eventId(booking.getEventId())
                .status(booking.getStatus())
                .totalPrice(booking.getTotalPrice())
                .createdAt(booking.getCreatedAt())
                .items(bookingItems.stream()
                        .map(bookingItem ->EventSeatResponseDto.builder()
                                .eventSeatId(bookingItem.getEventSeatId())
                                .price(bookingItem.getPrice())
                                .build())
                        .toList())
                .build();
    }

    @Transactional
    public BookingInfoResponseDto cancelBooking(UUID userId, UUID bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Booking with id " + bookingId + " not found"));

        if(!booking.getUserId().equals(userId)){
            throw new ForbiddenException("Access denied: this booking belongs to another user");
        }

        if(!booking.getStatus().equals(BookingStatus.PENDING)) {
            throw new IllegalStateException("Cannot cancel booking in status CONFIRMED. Only PENDING bookings can be cancelled");
        }


        List<BookingItem> bookingItems = bookingItemRepository.findAllByBookingId(bookingId);
        List<UUID> eventSeatIds = bookingItems.stream()
                .map(BookingItem::getEventSeatId)
                .toList();
        boolean released = redisService.release(
                bookingId,
                booking.getEventId(),
                eventSeatIds
        );

        if (!released) {
            throw new ReservationNotFoundException("Reservation for booking %s not found or already expired".formatted(bookingId));
        }

        booking.setStatus(BookingStatus.CANCELLED);

        return toInfoResponse(booking);
    }

    @Transactional
    public void expireBooking(UUID bookingId){
        Optional<Booking> bookingOpt = bookingRepository.findById(bookingId);
        if(bookingOpt.isEmpty()){
            System.out.printf("Expired booking not found in DB: %s\n", bookingId);
            return;
        }

        int updated = bookingRepository.expireIfPending(
                bookingId,
                BookingStatus.PENDING,
                BookingStatus.EXPIRED
        );

        if(updated == 0){
            System.out.printf("Status of booking %d already not PENDING \n", bookingId);
        }
    }

    @Transactional
    public void handleBookingConfirmed(PaymentSucceededEvent event, UUID messageId) {
        int marked = processedEventRepository.markProcessed(messageId);
        if(marked == 0){
            return;
        }

        Booking booking = bookingRepository.findById(event.getBookingId())
                .orElseThrow(() -> new BookingNotFoundException("Booking with id " + event.getBookingId() + " not found"));

        List<UUID> eventSeatIds = booking.getItems().stream()
                .map(BookingItem::getEventSeatId)
                .toList();

        switch (booking.getStatus()){
            case CONFIRMED -> {
                return;
            }
            case PENDING -> confirmPendingBooking(booking, eventSeatIds);
            case EXPIRED -> confirmExpiredBooking(booking, eventSeatIds);
            case CANCELLED -> {
                refundPayment(booking);
            }
            default -> throw new IllegalStateException("Unexpected value: " + booking.getStatus());
        }
    }

    private void confirmPendingBooking(Booking booking, List<UUID> eventSeatIds){
        boolean sold = eventServiceClient.sellSeats(
                booking.getEventId(),
                eventSeatIds
        );

        if (!sold) {
            throw new IllegalStateException(
                    "Failed to sell seats for booking " + booking.getId()
            );
        }

        int confirmed = bookingRepository.confirmIfPending(
                booking.getId(),
                BookingStatus.PENDING,
                BookingStatus.CONFIRMED
        );

        if (confirmed == 0) {
            throw new IllegalStateException(
                    "Failed to confirm booking " + booking.getId()
            );
        }
        ticketService.createTickets(booking.getId());
    }

    private void confirmExpiredBooking(Booking booking, List<UUID> eventSeatIds){
        boolean reserved = redisService.reserve(
                booking.getId(),
                booking.getEventId(),
                eventSeatIds
        );

        if (!reserved) {
            refundPayment(booking);
            return;
        }

        try {
            boolean sold = eventServiceClient.sellSeats(
                    booking.getEventId(),
                    eventSeatIds
            );

            if (!sold) {
                redisService.release(
                        booking.getId(),
                        booking.getEventId(),
                        eventSeatIds
                );

                refundPayment(booking);
                return;
            }

            int confirmed = bookingRepository.confirmIfExpired(
                    booking.getId(),
                    BookingStatus.EXPIRED,
                    BookingStatus.CONFIRMED
            );

            if (confirmed == 0) {
                throw new IllegalStateException(
                        "Failed to confirm expired booking " + booking.getId()
                );
            }

        } catch (RuntimeException e) {
            try {
                redisService.release(
                        booking.getId(),
                        booking.getEventId(),
                        eventSeatIds
                );
            } catch (Exception releaseException) {
                e.addSuppressed(releaseException);
            }

            throw e;
        }
        ticketService.createTickets(booking.getId());
    }

    private void refundPayment(Booking booking){

    }

    private void validateEventSeats(BookingRequestDto request, List<EventSeatInfo> eventSeats) {
        List<UUID> requestedSeatIds = request.getEventSeatIds();

        if (requestedSeatIds == null || requestedSeatIds.isEmpty()) {
            throw new SeatValidationException(
                    "At least one event seat must be requested"
            );
        }

        Set<UUID> requestedIds = new HashSet<>(requestedSeatIds);

        if (requestedIds.size() != requestedSeatIds.size()) {
            throw new SeatValidationException(
                    "Request contains duplicate event seat ids"
            );
        }

        Set<UUID> returnedIds = eventSeats.stream()
                .map(EventSeatInfo::getEventSeatId)
                .collect(Collectors.toSet());

        if (!requestedIds.equals(returnedIds)) {
            throw new EventSeatMismatchException(
                    "Event service returned unexpected event seats"
            );
        }

        boolean anyUnavailable = eventSeats.stream()
                .anyMatch(seat -> seat.getStatus() != EventSeatStatus.AVAILABLE);
        if(anyUnavailable){
            throw new SeatsAlreadyReservedException("Any seats are no available");
        }
    }

    private BookingInfoResponseDto toInfoResponse(Booking booking) {
        return BookingInfoResponseDto.builder()
                .id(booking.getId())
                .eventId(booking.getEventId())
                .status(booking.getStatus())
                .totalPrice(booking.getTotalPrice())
                .createdAt(booking.getCreatedAt())
                .build();
    }
}
