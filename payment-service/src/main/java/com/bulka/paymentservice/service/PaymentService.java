package com.bulka.paymentservice.service;

import com.bulka.paymentservice.client.booking.BookingServiceClient;
import com.bulka.paymentservice.client.booking.dto.BookingPaymentDetailsResponse;
import com.bulka.paymentservice.dto.request.CreatePaymentRequest;
import com.bulka.paymentservice.dto.response.PaymentResponseDto;
import com.bulka.paymentservice.model.Payment;
import com.bulka.paymentservice.model.PaymentStatus;
import com.bulka.paymentservice.provider.PaymentProvider;
import com.bulka.paymentservice.provider.dto.PaymentResult;
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

    @Transactional
    public PaymentResponseDto createPayment(UUID userId, CreatePaymentRequest request) {
        BookingPaymentDetailsResponse booking = bookingServiceClient.getPaymentDetails(request.getBookingId());

        if(!booking.getUserId().equals(userId)){
            throw new RuntimeException( "Booking not found" );
//            throw new PaymentNotFoundException( "Booking not found" );
        }

        if(paymentRepository.existsByBookingIdAndStatus(
                request.getBookingId(),
                PaymentStatus.SUCCEEDED
        )){
//            throw new PaymentAlreadyExistsException( "Booking has already been paid" );
            throw new RuntimeException( "Booking has already been paid" );
        }

        Payment payment = Payment.builder()
                .bookingId(booking.getBookingId())
                .userId(booking.getUserId())
                .amount(booking.getAmount())
                .currency(booking.getCurrency())
                .status(PaymentStatus.PENDING)
                .provider(PROVIDER_NAME)
                .build();

        Payment savedPayment = paymentRepository.save(payment);
        PaymentResult result = paymentProvider.charge(
                savedPayment.getId(),
                savedPayment.getAmount(),
                Currency.getInstance(savedPayment.getCurrency())
        );

        if (result.getSuccessful()) {
            savedPayment.setStatus(PaymentStatus.SUCCEEDED);
            savedPayment.setProviderPaymentId(result.getProviderPaymentId());
            savedPayment.setFailureReason(null);
        }
        else {
            savedPayment.setStatus(PaymentStatus.FAILED);
            savedPayment.setFailureReason(result.getFailureReason());
        }

        return toResponse(savedPayment);
    }

    @Transactional(readOnly = true)
    public PaymentResponseDto getPayment(UUID paymentId, UUID userId) {
        Payment payment = paymentRepository.findById(paymentId)
//                .orElseThrow(() -> new PaymentNotFoundException( "Payment with id " + paymentId + " not found" ));
                .orElseThrow(() -> new RuntimeException("Payment with id " + paymentId + " not found"));
        if (!payment.getUserId().equals(userId)) {
//            throw new PaymentNotFoundException("Payment with id " + paymentId + " not found");
            throw new RuntimeException("Payment with id " + paymentId + " not found");
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
