package com.bulka.paymentservice.provider;

import com.bulka.paymentservice.provider.dto.PaymentProviderStatus;
import com.bulka.paymentservice.provider.dto.PaymentResult;
import com.bulka.paymentservice.provider.dto.RefundResult;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.UUID;

@Component
public class MockPaymentProvider  implements PaymentProvider {
    @Override
    public PaymentResult charge(UUID paymentId, BigDecimal amount, Currency currency) {
        return new PaymentResult(
                PaymentProviderStatus.SUCCEEDED,
                "mock-" + UUID.randomUUID(),
                null );
    }

    @Override
    public PaymentResult getPaymentStatus(UUID paymentId) {
        return new PaymentResult(
                PaymentProviderStatus.SUCCEEDED,
                "mock-" + UUID.randomUUID(),
                null );
    }

    @Override
    public RefundResult refund(
            UUID paymentId,
            BigDecimal amount,
            Currency currency,
            String idempotencyKey
    ) {
        return RefundResult.builder()
                .status(PaymentProviderStatus.SUCCEEDED)
                .refundId(UUID.randomUUID().toString())
                .build();
    }
}
