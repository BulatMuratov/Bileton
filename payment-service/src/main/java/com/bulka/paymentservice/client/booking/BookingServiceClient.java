package com.bulka.paymentservice.client.booking;

import com.bulka.paymentservice.client.booking.dto.BookingPaymentDetailsResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name="booking-service")
public interface BookingServiceClient {
    @GetMapping("/api/v1/internal/bookings/{bookingId}/payment-details")
    BookingPaymentDetailsResponse getPaymentDetails(@PathVariable UUID bookingId);
}
