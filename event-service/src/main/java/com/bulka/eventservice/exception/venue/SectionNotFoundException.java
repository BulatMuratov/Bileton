package com.bulka.eventservice.exception.venue;

public class SectionNotFoundException extends RuntimeException {
    public SectionNotFoundException(String message) {
        super(message);
    }
}
