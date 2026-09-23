package com.bulka.bookingservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final RedisTemplate<String, String> redisTemplate;

    private static final String RESERVATION_KEY_PREFIX = "reservation:";
    private static final String BOOKING_RESERVATION_KEY_PREFIX = "booking:reservation:";

//    @Value("${redis.ttl-minute}")
    private Duration reservedTtl = Duration.ofMinutes(10);

    private final DefaultRedisScript<Long> reserveScript;
    private final DefaultRedisScript<Long> releaseScript;


    public boolean reserve(
            UUID bookingId,
            UUID eventId,
            List<UUID> eventSeatIds
    ) {
        List<String> keys = buildingReservationKeys(bookingId, eventId, eventSeatIds);

        Long result = redisTemplate.execute(
                reserveScript,
                keys,
                bookingId.toString(),
                String.valueOf(reservedTtl.getSeconds())
        );

        return result != null && result == 1L;
    }

    public boolean release(
            UUID bookingId,
            UUID eventId,
            List<UUID> eventSeatIds
    ) {

        List<String> keys = buildingReservationKeys(bookingId, eventId, eventSeatIds);

        Long result = redisTemplate.execute(
                releaseScript,
                keys,
                bookingId.toString()
        );

        return result != null && result == 1L;
    }

    public List<UUID> getReservedEventSeatIds(UUID eventId){
        String pattern = RESERVATION_KEY_PREFIX + eventId + ":*";

        ScanOptions options = ScanOptions.scanOptions()
                .match(pattern)
                .count(100)
                .build();

        try (Cursor<String> cursor = redisTemplate.scan(options)) {
            List<UUID> eventSeatIds = new ArrayList<>();

            while (cursor.hasNext()) {
                String key = cursor.next();

                String eventSeatId = key.substring(
                        key.lastIndexOf(':') + 1
                );

                eventSeatIds.add(UUID.fromString(eventSeatId));
            }

            return eventSeatIds;
        }
    }

    private List<String> buildingReservationKeys(
            UUID bookingId,
            UUID eventId,
            List<UUID> eventSeatIds
    ) {
        String bookingReservationKey = BOOKING_RESERVATION_KEY_PREFIX + bookingId;

        List<String> keys = eventSeatIds.stream()
                .sorted()
                .map(seatId -> RESERVATION_KEY_PREFIX + eventId + ":" + seatId)
                .collect(Collectors.toCollection(ArrayList::new));
        keys.add(bookingReservationKey);

        return keys;
    }
}
