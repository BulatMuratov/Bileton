package com.bulka.paymentservice.provider;

import com.bulka.paymentservice.provider.dto.PaymentResult;
import com.bulka.paymentservice.provider.dto.RefundResult;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.UUID;

public interface PaymentProvider {

    PaymentResult charge(UUID paymentId, BigDecimal amount, Currency currency);
    PaymentResult getPaymentStatus(UUID paymentId);

    RefundResult refund(
            UUID paymentId,
            BigDecimal amount,
            Currency currency,
            String idempotencyKey
    );
}
