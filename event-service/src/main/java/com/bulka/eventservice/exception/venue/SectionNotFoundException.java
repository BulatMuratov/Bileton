package com.bulka.eventservice.exception.venue;

import com.bulka.eventservice.exception.ResourceNotFoundException;

public class SectionNotFoundException extends ResourceNotFoundException {
    public SectionNotFoundException(String message) {
        super(message);
    }
}
