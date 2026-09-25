package com.bulka.paymentservice.kafka.consumer;


import com.bulka.paymentservice.kafka.event.PaymentRefundEvent;
import com.bulka.paymentservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class BookingLifecycleListener {

    private final PaymentService paymentService;

    @KafkaListener(
            topics = "bookings.lifecycle",
            groupId = "payment-service",
            containerFactory = "eventKafkaListenerContainerFactory"
    )
    public void handle(PaymentRefundEvent event, @Header("messageId") UUID messageId) {
        paymentService.handleRefundEvent(event, messageId);
    }
}
