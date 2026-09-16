package com.bulka.bookingservice.repository;

import com.bulka.bookingservice.model.Booking;
import com.bulka.bookingservice.model.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BookingRepository extends JpaRepository<Booking, UUID> {
    List<Booking> findAllByUserId(UUID userId);

    @Modifying
    @Query("""
    update Booking b
    set b.status = :expired
    where b.id = :bookingId
      and b.status = :pending
    """)
    int expireIfPending(
            UUID bookingId,
            BookingStatus pending,
            BookingStatus expired
    );

    @Modifying
    @Query("""
    update Booking b
    set b.status = :confirmed
    where b.id = :bookingId
    and b.status = :pending
        """)
    int confirmIfPending(
            UUID bookingId,
            BookingStatus pending,
            BookingStatus confirmed
    );

    @Modifying
    @Query("""
        update Booking b
        set b.status = :confirmed
        where b.id = :bookingId
          and b.status = :expired
        """)
    int confirmIfExpired(
            UUID bookingId,
            BookingStatus expired,
            BookingStatus confirmed
    );
}
