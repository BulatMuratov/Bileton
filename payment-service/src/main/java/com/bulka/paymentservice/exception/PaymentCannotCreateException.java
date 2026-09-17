package com.bulka.paymentservice.exception;

public class PaymentCannotCreateException extends RuntimeException {
    public PaymentCannotCreateException(String message) {
        super(message);
    }
}
