package com.bulka.bookingservice.repository;

import com.bulka.bookingservice.dto.projection.BookingItemProjection;
import com.bulka.bookingservice.model.booking.BookingItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BookingItemRepository extends JpaRepository<BookingItem, UUID> {

    @Query("""
    select new com.bulka.bookingservice.dto.projection.BookingItemProjection(
        bi.eventSeatId,
        bi.price
    )
    from BookingItem bi
    where bi.booking.id = :bookingId
    """)
    List<BookingItemProjection> findDetailsByBookingId(UUID bookingId);

    List<BookingItem> findAllByBookingId(UUID bookingId);
}
