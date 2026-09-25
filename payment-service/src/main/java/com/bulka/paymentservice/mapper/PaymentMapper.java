package com.bulka.paymentservice.mapper;

import com.bulka.paymentservice.client.booking.dto.BookingPaymentDetailsResponse;
import com.bulka.paymentservice.model.Payment;
import com.bulka.paymentservice.model.PaymentStatus;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class PaymentMapper {

    public Payment toEntity(UUID paymentId, BookingPaymentDetailsResponse booking, String PROVIDER_NAME){
        return Payment.builder()
                .id(paymentId)
                .bookingId(booking.getBookingId())
                .userId(booking.getUserId())
                .amount(booking.getAmount())
                .currency(booking.getCurrency())
                .status(PaymentStatus.PENDING)
                .provider(PROVIDER_NAME)
                .build();
    }
}
