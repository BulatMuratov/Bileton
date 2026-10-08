package com.bulka.eventservice.repository.venue;

import com.bulka.eventservice.dto.projection.venue.VenueDetailsProjection;
import com.bulka.eventservice.model.venue.Venue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface VenueRepository extends JpaRepository<Venue, UUID> {
    @Query("""
    select new com.bulka.eventservice.dto.projection.venue.VenueDetailsProjection(
        v.id,
        v.name,
        v.description,
        v.dimensions.width,
        v.dimensions.height
    )
    from Venue v
    where v.id = :venueId
""")
    Optional<VenueDetailsProjection> findVenueDetailsById(UUID venueId);

    @Query("""
        select new com.bulka.eventservice.dto.projection.venue.VenueDetailsProjection(
                v.id,
                v.name,
                v.description,
                v.dimensions.width,
                v.dimensions.height
            )
            from Venue v
    """)
    List<VenueDetailsProjection> findAllVenuesSummary();
}
