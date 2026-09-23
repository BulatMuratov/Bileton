package com.bulka.eventservice.exception.event;

import com.bulka.eventservice.exception.ResourceNotFoundException;

public class EventNotFoundException extends ResourceNotFoundException {
    public EventNotFoundException(String message) {
        super(message);
    }
}
