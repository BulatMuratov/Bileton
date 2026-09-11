package com.bulka.bookingservice.exception;

import com.bulka.bookingservice.dto.ErrorResponse;
import com.bulka.bookingservice.exception.booking.BookingNotFoundException;
import com.bulka.bookingservice.exception.booking.EventSeatMismatchException;
import com.bulka.bookingservice.exception.booking.IllegalStateException;
import com.bulka.bookingservice.exception.booking.ReservationNotFoundException;
import com.bulka.bookingservice.exception.booking.SeatValidationException;
import com.bulka.bookingservice.exception.booking.SeatsAlreadyReservedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(SeatValidationException.class)
    public ResponseEntity<ErrorResponse> handleSeatValidationException(SeatValidationException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.builder()
                        .status(HttpStatus.BAD_REQUEST.value())
                        .error(ex.getMessage())
                        .message("Validation exception")
                        .build());
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(ForbiddenException e) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ErrorResponse.builder()
                        .status(HttpStatus.FORBIDDEN.value())
                        .error(e.getMessage())
                        .message("Access denied")
                        .build());
    }

    @ExceptionHandler(BookingNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleBookingNotFoundException(BookingNotFoundException e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse.builder()
                        .status(HttpStatus.NOT_FOUND.value())
                        .error(e.getMessage())
                        .message("The booking was not found")
                        .build());
    }

    @ExceptionHandler(SeatsAlreadyReservedException.class)
    public ResponseEntity<ErrorResponse> handleSeatsAlreadyReservedException(SeatsAlreadyReservedException e) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ErrorResponse.builder()
                        .status(HttpStatus.CONFLICT.value())
                        .error(e.getMessage())
                        .message("The seats are taken")
                        .build());
    }


    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalStateException(IllegalStateException e) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ErrorResponse.builder()
                        .status(HttpStatus.CONFLICT.value())
                        .error(e.getMessage())
                        .message("State error")
                        .build());
    }

    @ExceptionHandler(ReservationNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleIllegalStateException(ReservationNotFoundException e) {
        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(ErrorResponse.builder()
                    .status(HttpStatus.CONFLICT.value())
                    .error(e.getMessage())
                    .message("Reservation not found")
                    .build());
    }

    @ExceptionHandler(EventSeatMismatchException.class)
    public ResponseEntity<ErrorResponse> handleException(EventSeatMismatchException exception) {
        return ResponseEntity
                .status(HttpStatus.BAD_GATEWAY)
                .body(ErrorResponse.builder()
                        .status(HttpStatus.BAD_GATEWAY.value())
                        .error(exception.getMessage())
                        .message(exception.getMessage())
                        .build());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception exception) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.builder()
                        .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .error(exception.getMessage())
                        .message(exception.getMessage())
                        .build());
    }

}
