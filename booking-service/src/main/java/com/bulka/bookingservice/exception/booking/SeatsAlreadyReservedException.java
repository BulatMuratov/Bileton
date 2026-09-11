package com.bulka.bookingservice.exception.booking;

public class SeatsAlreadyReservedException extends RuntimeException {
    public SeatsAlreadyReservedException(String message) {
        super(message);
    }
}
