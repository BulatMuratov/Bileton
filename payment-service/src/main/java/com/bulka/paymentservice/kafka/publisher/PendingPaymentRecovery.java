package com.bulka.paymentservice.kafka.publisher;

import com.bulka.paymentservice.model.Payment;
import com.bulka.paymentservice.model.PaymentStatus;
import com.bulka.paymentservice.provider.PaymentProvider;
import com.bulka.paymentservice.provider.dto.PaymentProviderStatus;
import com.bulka.paymentservice.provider.dto.PaymentResult;
import com.bulka.paymentservice.repository.PaymentRepository;
import com.bulka.paymentservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class PendingPaymentRecovery {

    private static final int RECOVERY_DELAY_MINUTES = 1;

    private final PaymentRepository paymentRepository;
    private final PaymentProvider paymentProvider;
    private final PaymentService paymentService;

    @Scheduled(fixedDelay = 30_000)
    public void recover() {
        OffsetDateTime threshold =
                OffsetDateTime.now().minusMinutes(RECOVERY_DELAY_MINUTES);

        List<Payment> payments =
                paymentRepository
                        .findTop100ByStatusAndCreatedAtBeforeOrderByCreatedAtAsc(
                                PaymentStatus.PENDING,
                                threshold
                        );

        for (Payment payment : payments) {
            recover(payment);
        }
    }

    private void recover(Payment payment) {
        try {
            PaymentResult result =
                    paymentProvider.getPaymentStatus(payment.getId());

            if(result.getStatus() == PaymentProviderStatus.PENDING){
                return;
            }

            paymentService.finalizePayment(
                    payment.getId(),
                    result
            );

        } catch (Exception e) {
            log.error(
                    "Failed to recover payment {}",
                    payment.getId(),
                    e
            );
        }
    }
}