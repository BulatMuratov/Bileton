package com.bulka.bookingservice.kafka.consumer;

import com.bulka.bookingservice.kafka.event.PaymentSucceededEvent;
import com.bulka.bookingservice.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PaymentLifecycleListener {

    private final BookingService bookingService;

    @KafkaListener(
            topics = "payments.lifecycle",
            groupId = "booking-service",
            containerFactory = "paymentKafkaListenerContainerFactory"
    )
    public void handle(PaymentSucceededEvent event, @Header("messageId") UUID messageId) {
        System.out.println("OKOK");
        bookingService.handleBookingConfirmed(event, messageId);
    }
}

//    @KafkaHandler
//    public void handlePaymentFailed(PaymentFailedEvent event){}
