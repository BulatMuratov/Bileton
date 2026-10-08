package com.bulka.bookingservice.repository;

import com.bulka.bookingservice.dto.projection.BookingDetailsProjection;
import com.bulka.bookingservice.dto.projection.BookingInfoProjection;
import com.bulka.bookingservice.dto.projection.BookingPaymentDetailsProjection;
import com.bulka.bookingservice.model.booking.Booking;
import com.bulka.bookingservice.model.booking.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BookingRepository extends JpaRepository<Booking, UUID> {

    @Query("""
    select new com.bulka.bookingservice.dto.projection.BookingPaymentDetailsProjection(
        b.id,
        b.userId,
        b.totalPrice
    )
    from Booking b
    where b.id = :bookingId
""")
    Optional<BookingPaymentDetailsProjection> findPaymentDetailsById(UUID bookingId);

    @Query("""
    select new com.bulka.bookingservice.dto.projection.BookingDetailsProjection(
        b.id,
        b.userId,
        b.eventId,
        b.status,
        b.totalPrice,
        b.createdAt
    )
    from Booking b
    where b.id = :bookingId
    """)
    Optional<BookingDetailsProjection> findDetailsById(UUID bookingId);

    @Query("""
    select new com.bulka.bookingservice.dto.projection.BookingInfoProjection(
        b.id,
        b.eventId,
        b.status,
        b.totalPrice,
        b.createdAt
    )
    from Booking b
    where b.userId = :userId
    """)
    List<BookingInfoProjection> findInfoByUserId(UUID userId);

    List<Booking> findAllByUserId(UUID userId);

    @Modifying
    @Query("""
        update Booking b
        set b.status = :newStatus
        where b.id = :bookingId
          and b.status = :currentStatus
        """)
    int updateStatusIfCurrent(
            UUID bookingId,
            BookingStatus currentStatus,
            BookingStatus newStatus
    );
}
