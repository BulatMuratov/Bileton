package com.bulka.bookingservice.controller;

import com.bulka.bookingservice.dto.request.BookingRequestDto;
import com.bulka.bookingservice.dto.response.BookingDetailsResponseDto;

import com.bulka.bookingservice.dto.response.BookingInfoResponseDto;
import com.bulka.bookingservice.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<BookingDetailsResponseDto> createBooking(
            @RequestBody BookingRequestDto requestDto,
            Authentication authentication
    ) {
        UUID userId = UUID.fromString(authentication.getName());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(bookingService.createBooking(userId, requestDto));
    }

    @GetMapping
    public ResponseEntity<List<BookingInfoResponseDto>> getBookingsForUser(
            Authentication authentication
    ) {
        UUID userId = UUID.fromString(authentication.getName());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(bookingService.getBookingsByUser(userId));
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingDetailsResponseDto> getBookingById(
            @PathVariable UUID bookingId,
            Authentication authentication
    ) {
        UUID userId = UUID.fromString(authentication.getName());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(bookingService.getBookingById(bookingId, userId));
    }

    @PostMapping("/{bookingId}/cancel")
    public ResponseEntity<?> cancelBooking(
            @PathVariable UUID bookingId,
            Authentication authentication
    ) {
        UUID userId = UUID.fromString(authentication.getName());

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(bookingService.cancelBooking(userId, bookingId));
    }



}
