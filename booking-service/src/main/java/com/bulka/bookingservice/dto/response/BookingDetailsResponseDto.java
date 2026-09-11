package com.bulka.bookingservice.dto.response;

import com.bulka.bookingservice.model.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingDetailsResponseDto {
    private UUID id;
    private UUID eventId;
    private BookingStatus status;
    private BigDecimal totalPrice;
    private OffsetDateTime createdAt;
    private List<EventSeatResponseDto> items;
}