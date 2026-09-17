package com.bulka.eventservice.repository;

import com.bulka.eventservice.model.EventSeat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EventSeatRepository extends JpaRepository<EventSeat, UUID> {
    List<EventSeat> findAllByEventId(UUID eventId);
    List<EventSeat> findAllByEventIdAndIdIn(UUID eventId, List<UUID> seatIds);

    @Modifying
    @Query(value = """
        UPDATE event_seats
        SET status = 'SOLD'
        WHERE event_id = :eventId
        AND id IN (:eventSeatIds)
        AND status = 'AVAILABLE'
        """, nativeQuery = true)
    int sellAvailableSeats(UUID eventId, List<UUID> eventSeatIds);
}
