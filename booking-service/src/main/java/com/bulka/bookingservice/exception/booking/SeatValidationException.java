package com.bulka.bookingservice.exception.booking;

public class SeatValidationException extends RuntimeException {
    public SeatValidationException(String message) {
        super(message);
    }
}
