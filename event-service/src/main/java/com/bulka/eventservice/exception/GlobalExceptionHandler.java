package com.bulka.eventservice.exception;

import com.bulka.eventservice.dto.ErrorResponse;
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
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
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
                        .error("Missing Request Header Exception")
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
                        .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
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
                        .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                        .code(ApiErrorCode.INVALID_ARGUMENT.name())
                        .message(e.getMessage())
                        .build());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalStateException(IllegalStateException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.builder()
                        .status(HttpStatus.BAD_REQUEST.value())
                        .error("State error")
                        .code(ApiErrorCode.INVALID_STATE.name())
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
    public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse.builder()
                        .status(HttpStatus.NOT_FOUND.value())
                        .error("Resource not found.")
                        .code(resolveNotFoundCode(e).name())
                        .message(e.getMessage())
                        .build());
    }

    @ExceptionHandler({DuplicateSeatException.class, DuplicateSectionException.class})
    public ResponseEntity<ErrorResponse> handleDuplicateException(RuntimeException e) {
        ApiErrorCode code = e instanceof DuplicateSeatException
                ? ApiErrorCode.DUPLICATE_SEAT
                : ApiErrorCode.DUPLICATE_SECTION;

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ErrorResponse.builder()
                        .status(HttpStatus.CONFLICT.value())
                        .error("Duplicate entities.")
                        .code(code.name())
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
                        .code(ApiErrorCode.INVALID_EVENT_STATE.name())
                        .message(e.getMessage())
                        .build());
    }

    @ExceptionHandler(SeatsNotAvailableException.class)
    public ResponseEntity<ErrorResponse> handleSeatsNotAvailableException(SeatsNotAvailableException e) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ErrorResponse.builder()
                        .status(HttpStatus.CONFLICT.value())
                        .error("Seats not available.")
                        .code(ApiErrorCode.SEATS_NOT_AVAILABLE.name())
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
                        .code(ApiErrorCode.EVENT_SEAT_DATA_INTEGRITY.name())
                        .message(e.getMessage())
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

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException e) {
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

    private ApiErrorCode resolveNotFoundCode(ResourceNotFoundException e) {
        if (e instanceof VenueNotFoundException)     return ApiErrorCode.VENUE_NOT_FOUND;
        if (e instanceof SectionNotFoundException)   return ApiErrorCode.SECTION_NOT_FOUND;
        if (e instanceof SeatNotFoundException)      return ApiErrorCode.SEAT_NOT_FOUND;
        if (e instanceof EventNotFoundException)     return ApiErrorCode.EVENT_NOT_FOUND;
        if (e instanceof EventSeatNotFoundException) return ApiErrorCode.EVENT_SEAT_NOT_FOUND;
        return ApiErrorCode.RESOURCE_NOT_FOUND;
    }
}