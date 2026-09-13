package com.bulka.paymentservice.provider;

import com.bulka.paymentservice.provider.dto.PaymentResult;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.UUID;

public interface PaymentProvider {

    PaymentResult charge(UUID paymentId, BigDecimal amount, Currency currency);
}
