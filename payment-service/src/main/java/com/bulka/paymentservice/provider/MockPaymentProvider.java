package com.bulka.paymentservice.provider;

import com.bulka.paymentservice.provider.dto.PaymentResult;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.UUID;

@Component
public class MockPaymentProvider  implements PaymentProvider {
    @Override
    public PaymentResult charge(UUID paymentId, BigDecimal amount, Currency currency) {
        return new PaymentResult(
                true,
                "mock-" + UUID.randomUUID(),
                null );
    }
}
