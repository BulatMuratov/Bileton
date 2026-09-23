package com.bulka.eventservice.exception.venue;

import com.bulka.eventservice.exception.ResourceNotFoundException;

public class SeatNotFoundException extends ResourceNotFoundException {
    public SeatNotFoundException(String message) {
        super(message);
    }
}
