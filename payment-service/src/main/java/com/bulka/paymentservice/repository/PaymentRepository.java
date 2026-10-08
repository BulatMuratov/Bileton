package com.bulka.paymentservice.repository;

import com.bulka.paymentservice.dto.projection.PaymentProjection;
import com.bulka.paymentservice.model.Payment;
import com.bulka.paymentservice.model.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    @Query("""
    select new com.bulka.paymentservice.dto.projection.PaymentProjection(
        p.id,
        p.bookingId,
        p.amount,
        p.currency,
        p.status,
        p.createdAt
    )
    from Payment p
    where p.userId = :userId
    """)
    List<PaymentProjection> findAllProjectionByUserId(UUID userId);

    @Query("""
    select new com.bulka.paymentservice.dto.projection.PaymentProjection(
        p.id,
        p.bookingId,
        p.amount,
        p.currency,
        p.status,
        p.createdAt
    )
    from Payment p
    where p.id = :paymentId
      and p.userId = :userId
    """)
    Optional<PaymentProjection> findByIdAndUserId(
            UUID paymentId,
            UUID userId
    );

    boolean existsByBookingIdAndStatus(UUID bookingId, PaymentStatus paymentStatus);
    List<Payment> findAllByUserId(UUID userId);
    List<Payment> findTop100ByStatusAndCreatedAtBeforeOrderByCreatedAtAsc(PaymentStatus paymentStatus, OffsetDateTime createdAt);
}
