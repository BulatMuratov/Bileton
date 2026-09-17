package com.bulka.paymentservice.provider.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentResult {
    Boolean successful;
    String providerPaymentId;
    String failureReason;
}
