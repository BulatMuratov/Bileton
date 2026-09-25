package com.bulka.bookingservice.repository;

import com.bulka.bookingservice.model.booking.BookingItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BookingItemRepository extends JpaRepository<BookingItem, UUID> {

    List<BookingItem> findAllByBookingId(UUID bookingId);
}
