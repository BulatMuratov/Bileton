package com.bulka.eventservice.exception.venue;

public class DuplicateSeatException extends RuntimeException {
    public DuplicateSeatException(String message) {
        super(message);
    }
}
