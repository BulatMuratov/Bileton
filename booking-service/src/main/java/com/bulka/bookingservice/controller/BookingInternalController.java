package com.bulka.bookingservice.controller;

import com.bulka.bookingservice.dto.internal.response.BookingPaymentDetailsResponse;
import com.bulka.bookingservice.service.BookingInternalService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/internal/bookings/")
@RequiredArgsConstructor
public class BookingInternalController {
    private final BookingInternalService bookingInternalService;

    @GetMapping("/{bookingId}/payment-details")
    public BookingPaymentDetailsResponse getPaymentDetails(@PathVariable UUID bookingId){
        return bookingInternalService.getBookingPaymentDetails(bookingId);
    }
}
