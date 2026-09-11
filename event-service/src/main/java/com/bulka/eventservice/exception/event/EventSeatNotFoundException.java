package com.bulka.eventservice.exception.event;

public class EventSeatNotFoundException extends RuntimeException {
    public EventSeatNotFoundException(String message) {
        super(message);
    }
}
