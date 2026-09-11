package com.bulka.bookingservice.exception.booking;

public class EventSeatMismatchException extends RuntimeException {
    public EventSeatMismatchException(String message) {
        super(message);
    }
}
