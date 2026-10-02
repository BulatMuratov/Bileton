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
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.builder()
                        .status(HttpStatus.BAD_REQUEST.value())
                        .error("Serialization Error")
                        .code(ApiErrorCode.MALFORMED_JSON.name())
                        .message(ex.getMessage())
                        .build());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.builder()
                        .status(HttpStatus.BAD_REQUEST.value())
                        .error("Validation Failed")
                        .code(ApiErrorCode.VALIDATION_FAILED.name())
                        .message(ex.getMessage())
                        .build());
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<ErrorResponse> handleMissingRequestHeaderException(MissingRequestHeaderException ex) {
        ApiErrorCode code = "Idempotency-Key".equalsIgnoreCase(ex.getHeaderName())
                ? ApiErrorCode.MISSING_IDEMPOTENCY_KEY
                : ApiErrorCode.MISSING_REQUEST_HEADER;

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.builder()
                        .status(HttpStatus.BAD_REQUEST.value())
                        .error("Missing Request Header")
                        .code(code.name())
                        .message(ex.getMessage())
                        .build());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        Class<?> requiredType = ex.getRequiredType();
        ApiErrorCode code = ApiErrorCode.INVALID_UUID;

        if (requiredType != null && requiredType.isEnum()) {
            code = ApiErrorCode.INVALID_ENUM_VALUE;
        } else if (requiredType != null && requiredType.getSimpleName().contains("Date")) {
            code = ApiErrorCode.INVALID_DATE_FORMAT;
        }

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.builder()
                        .status(HttpStatus.BAD_REQUEST.value())
                        .error("Invalid Argument Type")
                        .code(code.name())
                        .message(ex.getMessage())
                        .build());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.builder()
                        .status(HttpStatus.BAD_REQUEST.value())
                        .error("Bad Request")
                        .code(ApiErrorCode.INVALID_ARGUMENT.name())
                        .message(e.getMessage())
                        .build());
    }

    @ExceptionHandler(SeatValidationException.class)
    public ResponseEntity<ErrorResponse> handleSeatValidationException(SeatValidationException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.builder()
                        .status(HttpStatus.BAD_REQUEST.value())
                        .error("Seat Validation Failed")
                        .code(ApiErrorCode.SEAT_VALIDATION_FAILED.name())
                        .message(ex.getMessage())
                        .build());
    }

    @ExceptionHandler({BookingNotFoundException.class, ReservationNotFoundException.class, TicketNotFoundException.class})
    public ResponseEntity<ErrorResponse> handleResourceNotFound(RuntimeException e) {
        ApiErrorCode code = e instanceof BookingNotFoundException
                ? ApiErrorCode.BOOKING_NOT_FOUND
                : ApiErrorCode.RESERVATION_NOT_FOUND;

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse.builder()
                        .status(HttpStatus.NOT_FOUND.value())
                        .error("Resource Not Found")
                        .code(code.name())
                        .message(e.getMessage())
                        .build());
    }

    @ExceptionHandler(SeatsAlreadyReservedException.class)
    public ResponseEntity<ErrorResponse> handleSeatsAlreadyReservedException(SeatsAlreadyReservedException e) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ErrorResponse.builder()
                        .status(HttpStatus.CONFLICT.value())
                        .error("Conflict")
                        .code(ApiErrorCode.SEATS_ALREADY_RESERVED.name())
                        .message(e.getMessage())
                        .build());
    }

    @ExceptionHandler(com.bulka.bookingservice.exception.booking.IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalStateException(com.bulka.bookingservice.exception.booking.IllegalStateException e) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ErrorResponse.builder()
                        .status(HttpStatus.CONFLICT.value())
                        .error("State Conflict")
                        .code(ApiErrorCode.INVALID_STATE.name())
                        .message(e.getMessage())
                        .build());
    }

    @ExceptionHandler(EventSeatMismatchException.class)
    public ResponseEntity<ErrorResponse> handleEventSeatMismatchException(EventSeatMismatchException exception) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.builder()
                        .status(HttpStatus.BAD_REQUEST.value())
                        .error("Data Mismatch")
                        .code(ApiErrorCode.EVENT_SEAT_MISMATCH.name())
                        .message(exception.getMessage())
                        .build());
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationException e) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ErrorResponse.builder()
                        .status(HttpStatus.UNAUTHORIZED.value())
                        .error("Unauthorized")
                        .code(ApiErrorCode.UNAUTHORIZED.name())
                        .message("Authentication is required")
                        .build());
    }

    @ExceptionHandler({AccessDeniedException.class, ForbiddenException.class})
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(Exception e) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ErrorResponse.builder()
                        .status(HttpStatus.FORBIDDEN.value())
                        .error("Forbidden")
                        .code(ApiErrorCode.FORBIDDEN.name())
                        .message("You do not have permission to perform this action")
                        .build());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception e) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.builder()
                        .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                        .error("Internal Server Error")
                        .code(ApiErrorCode.INTERNAL_SERVER_ERROR.name())
                        .message(e.getMessage())
                        .build());
    }
}
