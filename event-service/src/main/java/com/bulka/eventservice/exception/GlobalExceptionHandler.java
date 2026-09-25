package com.bulka.eventservice.exception;

import com.bulka.eventservice.exception.event.EventNotFoundException;
import com.bulka.eventservice.exception.event.EventSeatDataIntegrityException;
import com.bulka.eventservice.exception.event.EventSeatNotFoundException;
import com.bulka.eventservice.exception.event.InvalidEventStateException;
import com.bulka.eventservice.exception.event.SeatsNotAvailableException;
import com.bulka.eventservice.exception.venue.DuplicateSeatException;
import com.bulka.eventservice.exception.venue.DuplicateSectionException;
import com.bulka.eventservice.exception.venue.SeatNotFoundException;
import com.bulka.eventservice.exception.venue.SectionNotFoundException;
import com.bulka.eventservice.exception.venue.VenueNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.bulka.eventservice.dto.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.builder()
                        .status(HttpStatus.BAD_REQUEST.value())
                        .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                        .message(e.getMessage())
                        .build());
    }

    @ExceptionHandler({
            VenueNotFoundException.class,
            SectionNotFoundException.class,
            SeatNotFoundException.class,
            EventNotFoundException.class,
            EventSeatNotFoundException.class,
    })
    public ResponseEntity<ErrorResponse> handleVenueDoesNotExistsException(ResourceNotFoundException e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse.builder()
                        .status(HttpStatus.NOT_FOUND.value())
                        .error("Resource not found.")
                        .message(e.getMessage())
                        .build());
    }

    @ExceptionHandler({DuplicateSeatException.class, DuplicateSectionException.class})
    public ResponseEntity<ErrorResponse> handleDuplicateException(RuntimeException e) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ErrorResponse.builder()
                        .status(HttpStatus.CONFLICT.value())
                        .error("Duplicate entities.")
                        .message(e.getMessage())
                        .build());
    }

    @ExceptionHandler(InvalidEventStateException.class)
    public ResponseEntity<ErrorResponse> handleInvalidEventStateException(InvalidEventStateException e) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ErrorResponse.builder()
                        .status(HttpStatus.CONFLICT.value())
                        .error("State exception.")
                        .message(e.getMessage())
                        .build());
    }

    @ExceptionHandler(SeatsNotAvailableException.class)
    public ResponseEntity<ErrorResponse> handleInvalidEventStateException(SeatsNotAvailableException e) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ErrorResponse.builder()
                        .status(HttpStatus.CONFLICT.value())
                        .error("Seats not available.")
                        .message(e.getMessage())
                        .build());
    }

    @ExceptionHandler(EventSeatDataIntegrityException.class)
    public ResponseEntity<ErrorResponse> handleEventSeatDataIntegrityException(EventSeatDataIntegrityException e) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.builder()
                        .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .error("Data inconsistency")
                        .message(e.getMessage())
                        .build());
    }

}
