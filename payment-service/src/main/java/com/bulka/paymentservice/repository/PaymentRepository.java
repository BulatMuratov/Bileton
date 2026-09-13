package com.bulka.paymentservice.repository;

import com.bulka.paymentservice.model.Payment;
import com.bulka.paymentservice.model.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    boolean existsByBookingIdAndStatus(UUID bookingId, PaymentStatus paymentStatus);
    List<Payment> findAllByUserId(UUID userId);
}
