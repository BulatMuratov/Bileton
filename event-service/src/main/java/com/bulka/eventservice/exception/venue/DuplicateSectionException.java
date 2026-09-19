package com.bulka.eventservice.exception.venue;

public class DuplicateSectionException extends RuntimeException {
    public DuplicateSectionException(String message) {
        super(message);
    }
}
