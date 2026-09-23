package com.bulka.eventservice.exception.venue;

import com.bulka.eventservice.exception.ResourceNotFoundException;

public class VenueNotFoundException extends ResourceNotFoundException {
    public VenueNotFoundException(String message) {
        super(message);
    }
}
