package com.bulka.bookingservice.kafka.consumer;

import com.bulka.bookingservice.kafka.dto.PaymentFailedEvent;
import com.bulka.bookingservice.kafka.dto.PaymentSucceededEvent;
import com.bulka.bookingservice.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@KafkaListener(id="payment-lifecycle-id", topics="payments.lifecycle", groupId="booking-service")
public class PaymentLifecycleListener {

    private final BookingService bookingService;
    @KafkaHandler
    public void handlePaymentSucceeded(PaymentSucceededEvent event){
        bookingService.handleBookingConfirmed(event);
    }

    @KafkaHandler
    public void handlePaymentFailed(PaymentFailedEvent event){}
}
