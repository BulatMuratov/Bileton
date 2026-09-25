package com.bulka.paymentservice.provider.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RefundResult {
    private PaymentProviderStatus status;
    private String refundId;
    private String failureReason;
}