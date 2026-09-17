package com.bulka.paymentservice.exception;

import com.bulka.paymentservice.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(PaymentCannotCreateException.class)
    public ResponseEntity<ErrorResponse> handlePaymentCannotCreateException(PaymentCannotCreateException exception){
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse.builder()
                        .status(HttpStatus.NOT_FOUND.value())
                        .message(exception.getMessage())
                        .error("Payment cannot create")
                        .build());
    }

    @ExceptionHandler(PaymentNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePaymentNotFoundException(PaymentNotFoundException exception){
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse.builder()
                        .status(HttpStatus.NOT_FOUND.value())
                        .message(exception.getMessage())
                        .error("Payment not found")
                        .build());
    }

    @ExceptionHandler(SuccessPaymentAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleSuccessPaymentAlreadyExistsException(SuccessPaymentAlreadyExistsException exception){
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ErrorResponse.builder()
                        .status(HttpStatus.CONFLICT.value())
                        .message(exception.getMessage())
                        .error("Success payment already exists")
                        .build());
    }
}
