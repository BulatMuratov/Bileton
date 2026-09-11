package com.bulka.eventservice.repository;

import com.bulka.eventservice.model.EventSeat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EventSeatRepository extends JpaRepository<EventSeat, UUID> {
    List<EventSeat> findAllByEventId(UUID eventId);
    List<EventSeat> findAllByEventIdAndIdIn(UUID eventId, List<UUID> seatIds);
}
