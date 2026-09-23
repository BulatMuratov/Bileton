package com.bulka.eventservice.exception.event;

import com.bulka.eventservice.exception.ResourceNotFoundException;

public class EventSeatNotFoundException extends ResourceNotFoundException {
    public EventSeatNotFoundException(String message) {
        super(message);
    }
}
