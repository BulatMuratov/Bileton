package com.bulka.bookingservice.repository;

import com.bulka.bookingservice.model.booking.Booking;
import com.bulka.bookingservice.model.booking.BookingStatus;
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
