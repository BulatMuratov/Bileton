package com.bulka.bookingservice.listener;

import com.bulka.bookingservice.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class BookingExpirationListener implements MessageListener {

    private final BookingService bookingService;

    private static final String BOOKING_RESERVATION_KEY_PREFIX = "booking:reservation:";

    @Override
    public void onMessage(Message message, byte @Nullable [] pattern) {
        String key = new String(message.getBody(), StandardCharsets.UTF_8);
        if(!key.startsWith(BOOKING_RESERVATION_KEY_PREFIX)) {
            return;
        }
        try{
            String bookingIdStr = key.substring(BOOKING_RESERVATION_KEY_PREFIX.length());
            UUID bookingId = UUID.fromString(bookingIdStr);

            bookingService.expireBooking(bookingId);
        } catch (IllegalArgumentException ex){
            System.out.printf("Failed to parse booking ID from expired key: %s%n", key);
        } catch (Exception e){
            System.out.printf("ERROR to parse booking ID from expired key: %s\n%s\n", key, e.getMessage());
        }
    }
}
