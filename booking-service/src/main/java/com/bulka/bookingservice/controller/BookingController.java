package com.bulka.bookingservice.controller;

import com.bulka.bookingservice.dto.request.BookingRequestDto;
import com.bulka.bookingservice.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public ResponseEntity<?> createBooking(BookingRequestDto requestDto){
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(bookingService.createBooking());
    }

    @GetMapping
    public ResponseEntity<List<?>> getBookingsForUser() {
        return null;
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<?> getBookingById(@PathVariable Long bookingId){
        return null;
    }

    @PostMapping("/{bookingId}/cancel")
    public ResponseEntity<?> cancelBooking(@RequestParam Long bookingId){
        return null;
    }



}
