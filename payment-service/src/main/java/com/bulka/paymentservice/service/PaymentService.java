package com.bulka.paymentservice.service;

import com.bulka.paymentservice.client.booking.BookingServiceClient;
import com.bulka.paymentservice.client.booking.dto.BookingPaymentDetailsResponse;
import com.bulka.paymentservice.dto.request.CreatePaymentRequest;
import com.bulka.paymentservice.dto.response.PaymentResponseDto;
import com.bulka.paymentservice.exception.PaymentCannotCreateException;
import com.bulka.paymentservice.exception.PaymentNotFoundException;
import com.bulka.paymentservice.exception.SuccessPaymentAlreadyExistsException;
import com.bulka.paymentservice.kafka.event.PaymentFailedEvent;
import com.bulka.paymentservice.kafka.event.PaymentRefundEvent;
import com.bulka.paymentservice.kafka.event.PaymentSucceededEvent;
import com.bulka.paymentservice.kafka.outbox.OutboxEventFactory;
import com.bulka.paymentservice.mapper.PaymentEventMapper;
import com.bulka.paymentservice.mapper.PaymentMapper;
import com.bulka.paymentservice.model.IdempotencyKey;
import com.bulka.paymentservice.model.Payment;
import com.bulka.paymentservice.model.PaymentStatus;
import com.bulka.paymentservice.provider.PaymentProvider;
import com.bulka.paymentservice.provider.dto.PaymentProviderStatus;
import com.bulka.paymentservice.provider.dto.PaymentResult;
import com.bulka.paymentservice.provider.dto.RefundResult;
import com.bulka.paymentservice.repository.IdempotencyKeyRepository;
import com.bulka.paymentservice.repository.OutboxEventRepository;
import com.bulka.paymentservice.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Currency;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private static final String PROVIDER_NAME = "MOCK";

    private final BookingServiceClient bookingServiceClient;
    private final PaymentProvider paymentProvider;
    private final PaymentRepository paymentRepository;
    private final IdempotencyKeyRepository idempotencyKeyRepository;

    private final OutboxEventRepository outboxEventRepository;
    private final OutboxEventFactory outboxEventFactory;

    private final PaymentMapper paymentMapper;
    private final PaymentEventMapper paymentEventMapper;

    public PaymentResponseDto createPayment(UUID userId, String idempotencyKey, CreatePaymentRequest request){
        UUID paymentId = preparePayment(userId, idempotencyKey, request);

        PaymentResult result;

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow();

        try {
            result = paymentProvider.charge(
                    payment.getId(),
                    payment.getAmount(),
                    Currency.getInstance(payment.getCurrency())
            );
        } catch (Exception e) {
            updatePaymentFailed(paymentId, "Payment provider unavailable");
            throw e;
        }

        return finalizePayment(paymentId, result);

    }

    @Transactional
    public UUID preparePayment(UUID userId, String idempotencyKey, CreatePaymentRequest request) {
        UUID paymentId = UUID.randomUUID();

        int inserted = idempotencyKeyRepository.insertIfAbsent(
                UUID.randomUUID(),
                userId,
                idempotencyKey,
                paymentId
        );

        if (inserted == 0) {
            IdempotencyKey key = idempotencyKeyRepository
                    .findByIdempotencyKeyAndUserId(idempotencyKey, userId)
                    .orElseThrow();

            return key.getPaymentId();
        }

        BookingPaymentDetailsResponse booking =
                bookingServiceClient.getPaymentDetails(request.getBookingId());

        if (!booking.getUserId().equals(userId)) {
            throw new PaymentCannotCreateException("Booking not found");
        }

        if (paymentRepository.existsByBookingIdAndStatus(
                request.getBookingId(),
                PaymentStatus.SUCCEEDED
        )) {
            throw new SuccessPaymentAlreadyExistsException(
                    "Booking has already been paid"
            );
        }

        Payment payment = paymentMapper.toEntity(paymentId, booking, PROVIDER_NAME);
        paymentRepository.save(payment);

        return paymentId;
    }

    @Transactional
    public void updatePaymentFailed(UUID paymentId, String reason) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() ->
                        new IllegalStateException("Payment not found: " + paymentId));

        if (payment.getStatus() != PaymentStatus.PENDING) {
            return;
        }

        payment.setStatus(PaymentStatus.FAILED);
        payment.setFailureReason(reason);

        PaymentFailedEvent event = paymentEventMapper.toFailedEvent(payment, reason);

        outboxEventRepository.save(
                outboxEventFactory.create(
                        UUID.randomUUID(),
                        "PaymentFailed",
                        "Payment",
                        payment.getId(),
                        event
                )
        );
    }

    @Transactional
    public PaymentResponseDto finalizePayment(
            UUID paymentId,
            PaymentResult result
    ) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow();

        if (payment.getStatus() != PaymentStatus.PENDING) {
            return toResponse(payment);
        }

        switch (result.getStatus()) {
            case SUCCEEDED -> {
                payment.setStatus(PaymentStatus.SUCCEEDED);
                payment.setProviderPaymentId(
                        result.getProviderPaymentId()
                );
                payment.setFailureReason(null);

                PaymentSucceededEvent event = paymentEventMapper.toSuccessEvent(payment);

                outboxEventRepository.save(
                        outboxEventFactory.create(
                                UUID.randomUUID(),
                                "PaymentSucceeded",
                                "Payment",
                                payment.getId(),
                                event
                        )
                );
            }
            case FAILED -> {
                payment.setStatus(PaymentStatus.FAILED);
                payment.setFailureReason(result.getFailureReason());

                PaymentFailedEvent event = paymentEventMapper.toFailedEvent(payment, result.getFailureReason());

                outboxEventRepository.save(
                        outboxEventFactory.create(
                                UUID.randomUUID(),
                                "PaymentFailed",
                                "Payment",
                                payment.getId(),
                                event
                        )
                );
            }
            case PENDING, UNKNOWN -> {
            }
        }
        return toResponse(payment);
    }

    public void handleRefundEvent(PaymentRefundEvent event, UUID messageId) {
        Payment payment = paymentRepository.findById(event.getPaymentId())
                .orElseThrow(() ->
                        new PaymentNotFoundException(
                                "Payment not found: " + event.getPaymentId()
                        )
                );

        if (payment.getStatus() == PaymentStatus.REFUNDED) {
            return;
        }

        if (payment.getStatus() != PaymentStatus.SUCCEEDED) {
            throw new IllegalStateException(
                    "Payment cannot be refunded. Status: "
                            + payment.getStatus()
            );
        }

        RefundResult result = paymentProvider.refund(
                payment.getId(),
                payment.getAmount(),
                Currency.getInstance(payment.getCurrency()),
                "refund-" + payment.getId()
        );

        if (result.getStatus() != PaymentProviderStatus.SUCCEEDED) {
            throw new IllegalStateException(
                    "Failed to refund payment " + payment.getId()
            );
        }

        payment.setStatus(PaymentStatus.REFUNDED);
        paymentRepository.save(payment);
    }

//    @Transactional
//    public PaymentResponseDto createPayment(UUID userId,String idempotencyKey, CreatePaymentRequest request) {
//        UUID paymentId = UUID.randomUUID();
//
//        int inserted = idempotencyKeyRepository.insertIfAbsent(
//                UUID.randomUUID(),
//                userId,
//                idempotencyKey,
//                paymentId
//        );
//        if(inserted == 0){
//            IdempotencyKey key = idempotencyKeyRepository.findByIdempotencyKeyAndUserId(idempotencyKey, userId)
//                    .orElseThrow(() -> new IllegalStateException("Idempotency key not found"));
//
//            return getPayment(key.getPaymentId(), userId);
//        }
//
//        BookingPaymentDetailsResponse booking = bookingServiceClient.getPaymentDetails(request.getBookingId());
//
//        if(!booking.getUserId().equals(userId)){
//            throw new PaymentCannotCreateException("Booking not found");
//        }
//
//        if(paymentRepository.existsByBookingIdAndStatus(
//                request.getBookingId(),
//                PaymentStatus.SUCCEEDED
//        )){
//            throw new SuccessPaymentAlreadyExistsException("Booking has already been paid");
//        }
//
//        Payment payment = Payment.builder()
//                .id(paymentId)
//                .bookingId(booking.getBookingId())
//                .userId(booking.getUserId())
//                .amount(booking.getAmount())
//                .currency(booking.getCurrency())
//                .status(PaymentStatus.PENDING)
//                .provider(PROVIDER_NAME)
//                .build();
//
//        Payment savedPayment = paymentRepository.save(payment);
//        PaymentResult result = paymentProvider.charge(
//                savedPayment.getId(),
//                savedPayment.getAmount(),
//                Currency.getInstance(savedPayment.getCurrency())
//        );
//
//        if (result.getSuccessful()) {
//            savedPayment.setStatus(PaymentStatus.SUCCEEDED);
//            savedPayment.setProviderPaymentId(result.getProviderPaymentId());
//            savedPayment.setFailureReason(null);
//
//            UUID eventId = UUID.randomUUID();
//            PaymentSucceededEvent paymentSucceededEvent = PaymentSucceededEvent.builder()
//                    .eventId(eventId)
//                    .paymentId(savedPayment.getId())
//                    .bookingId(request.getBookingId())
//                    .userId(savedPayment.getUserId())
//                    .amount(savedPayment.getAmount())
//                    .currency(savedPayment.getCurrency())
//                    .build();
//
//            outboxEventRepository.save(outboxEventFactory.create(
//                    UUID.randomUUID(),
//                    "PaymentSucceeded",
//                    "Payment",
//                    savedPayment.getId(),
//                    paymentSucceededEvent)
//            );
//        }
//        else {
//            savedPayment.setStatus(PaymentStatus.FAILED);
//            savedPayment.setFailureReason(result.getFailureReason());
//
//            UUID eventId = UUID.randomUUID();
//
//            PaymentFailedEvent event = new PaymentFailedEvent(
//                    eventId,
//                    savedPayment.getId(),
//                    savedPayment.getBookingId(),
//                    savedPayment.getUserId(),
//                    savedPayment.getAmount(),
//                    savedPayment.getCurrency(),
//                    savedPayment.getFailureReason()
//            );
//
//            outboxEventRepository.save(
//                    outboxEventFactory.create(
//                            eventId,
//                            "PaymentFailed",
//                            "Payment",
//                            savedPayment.getId(),
//                            event)
//            );
//        }
//
//        return toResponse(savedPayment);
//    }

    @Transactional(readOnly = true)
    public PaymentResponseDto getPayment(UUID paymentId, UUID userId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new PaymentNotFoundException("Payment with id " + paymentId + " not found"));
        if (!payment.getUserId().equals(userId)) {
            throw new PaymentNotFoundException("Payment with id " + paymentId + " not found");
        }

        return toResponse(payment);
    }

    @Transactional(readOnly = true)
    public List<PaymentResponseDto> getPayments(UUID userId) {
        return paymentRepository.findAllByUserId(userId).stream()
                .map(this::toResponse)
                .toList();
    }

    private PaymentResponseDto toResponse(Payment payment) {
        return PaymentResponseDto.builder()
                .id(payment.getId())
                .bookingId(payment.getBookingId())
                .amount(payment.getAmount())
                .currency(payment.getCurrency())
                .status(payment.getStatus())
                .provider(PROVIDER_NAME)
                .createdAt(payment.getCreatedAt())
                .build();
    }
}
