package com.bulka.eventservice.repository;

import com.bulka.eventservice.model.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SeatRepository extends JpaRepository<Seat, UUID> {
    List<Seat> findAllByVenueId(UUID id);
    Optional<Seat> findByIdAndVenueId(UUID id, UUID venueId);
    List<Seat> findAllByVenueIdAndIdIn(UUID id, List<UUID> seatIds);
}
