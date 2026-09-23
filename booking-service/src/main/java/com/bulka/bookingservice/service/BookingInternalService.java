package com.bulka.bookingservice.service;

import com.bulka.bookingservice.dto.internal.response.BookingPaymentDetailsResponse;
import com.bulka.bookingservice.dto.internal.response.ReservedSeatsResponse;
import com.bulka.bookingservice.exception.booking.BookingNotFoundException;
import com.bulka.bookingservice.model.Booking;
import com.bulka.bookingservice.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookingInternalService {

    private final BookingRepository bookingRepository;
    private final RedisService redisService;

    public BookingPaymentDetailsResponse getBookingPaymentDetails(UUID bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found " + bookingId));

        return BookingPaymentDetailsResponse.builder()
                .bookingId(booking.getId())
                .userId(booking.getUserId())
                .amount(booking.getTotalPrice())
                .currency("RUB")
                .build();
    }

    public ReservedSeatsResponse getReservedSeats(UUID eventId){
        return ReservedSeatsResponse.builder()
                .eventId(eventId)
                .reservedEventSeatsId(
                        redisService.getReservedEventSeatIds(eventId)
                )
                .build();
    }


}
