package com.bulka.bookingservice.service;

import com.bulka.bookingservice.dto.request.BookingRequestDto;
import com.bulka.bookingservice.model.Booking;
import com.bulka.bookingservice.model.BookingStatus;
import com.bulka.bookingservice.repository.BookingItemRepository;
import com.bulka.bookingservice.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookingService {

    public final BookingRepository bookingRepository;
    public final BookingItemRepository bookingItemRepository;

    private final StringRedisTemplate redisTemplate;
    private final static Duration RESERVED_TTL = Duration.ofMinutes(10);

    private final static DefaultRedisScript<String> RESERVE_SCRIPT = new DefaultRedisScript<>(
            """
            for _, key in ipairs(KEYS) do
                if redis.call('EXISTS', key) == 1 then
                    return 0
                end
            end
            
            for _, key in ipairs(KEYS) do
                redis.call('SET', key, ARGV[1], 'EX', ARGV[2])
            end
            
            return 1
            """,
            String.class);

    private final static DefaultRedisScript<String> RELEASE_SCRIPT = new DefaultRedisScript<>(
            """
            for _, key in ipairs(KEYS) do
                if redis.call('GET', key) ~= ARGV[1] then
                    return 0
                end
            end
            
            for _, key in ipairs(KEYS) do
                redis.call('DEL', key)
            end
            
            return 1
            """,
            String.class);

    @Transactional
    public void createBooking(UUID userId, BookingRequestDto bookingRequestDto){
        UUID bookingId = UUID.randomUUID();

        List<String> keys = bookingRequestDto.getEventSeatsIdList().stream()
                .sorted()
                .map(obj-> String.format("reservation:%s:%s",
                        bookingRequestDto.getEventId().toString(),
                        obj.toString()))
                .toList();;

        String result = redisTemplate.execute(RESERVE_SCRIPT, keys, bookingId.toString(), RESERVED_TTL);
        if(result == null || result.equals("0")){
            throw new RuntimeException("Seats were reserved");
        }

//        bookingRepository.save(Booking.builder()
//                        .id(bookingId)
//                        .userId(userId)
//                        .eventId(bookingRequestDto.getEventId())
//                        .status(BookingStatus.PENDING)
////                        .expiresAt(?)
//                        .totalPrice(bookingRequestDto.getEventSeatsIdList().stream()
//                                .map(obj -> ))
//                .build())

    }
}
