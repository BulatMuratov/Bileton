package com.bulka.eventservice.exception.event;

public class SeatsNotAvailableException extends RuntimeException{
    public SeatsNotAvailableException(String message) {
        super(message);
    }
}
