package com.bulka.eventservice.repository.venue;

import com.bulka.eventservice.model.venue.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SeatRepository extends JpaRepository<Seat, UUID> {
    List<Seat> findAllBySectionId(UUID sectionId);
    List<Seat> findAllBySectionIdIn(List<UUID> sectionIds);
}
