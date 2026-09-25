package com.bulka.paymentservice.mapper;

import com.bulka.paymentservice.kafka.event.PaymentFailedEvent;
import com.bulka.paymentservice.kafka.event.PaymentSucceededEvent;
import com.bulka.paymentservice.model.Payment;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class PaymentEventMapper {

    public PaymentSucceededEvent toSuccessEvent(Payment payment) {
        return PaymentSucceededEvent.builder()
                .eventId(UUID.randomUUID())
                .paymentId(payment.getId())
                .bookingId(payment.getBookingId())
                .userId(payment.getUserId())
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
                .build();
    }

    public PaymentFailedEvent toFailedEvent(Payment payment, String reason) {
        return PaymentFailedEvent.builder()
                .eventId(UUID.randomUUID())
                .paymentId(payment.getId())
                .bookingId(payment.getBookingId())
                .userId(payment.getUserId())
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
                .failureReason(reason)
                .build();
    }
}
