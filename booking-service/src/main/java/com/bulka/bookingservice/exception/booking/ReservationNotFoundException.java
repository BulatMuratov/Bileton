package com.bulka.bookingservice.exception.booking;

public class ReservationNotFoundException extends  RuntimeException {
    public ReservationNotFoundException(String message) {
        super(message);
    }
}
