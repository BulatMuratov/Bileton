package com.bulka.eventservice.repository.venue;

import com.bulka.eventservice.dto.projection.venue.SeatDetailsProjection;
import com.bulka.eventservice.model.venue.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Repository
public interface SeatRepository extends JpaRepository<Seat, UUID> {

    @Query("""
    select new com.bulka.eventservice.dto.projection.venue.SeatDetailsProjection(
        s.id,
        s.section.id,
        s.rowNumber,
        s.seatNumber,
        s.layout.x,
        s.layout.y,
        s.layout.width,
        s.layout.height,
        s.layout.rotation
    )
    from Seat s
    where s.section.id in :sectionIds
""")
    List<SeatDetailsProjection> findDetailsBySectionIds(
            Collection<UUID> sectionIds
    );

    List<Seat> findAllBySectionId(UUID sectionId);
    List<Seat> findAllBySectionIdIn(List<UUID> sectionIds);
}
