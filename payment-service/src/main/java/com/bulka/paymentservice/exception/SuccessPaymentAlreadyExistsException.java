package com.bulka.paymentservice.exception;

public class SuccessPaymentAlreadyExistsException extends RuntimeException {
    public SuccessPaymentAlreadyExistsException(String message) {
        super(message);
    }
}
