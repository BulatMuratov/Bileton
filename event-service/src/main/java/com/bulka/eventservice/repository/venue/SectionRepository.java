package com.bulka.eventservice.repository.venue;

import com.bulka.eventservice.dto.projection.venue.SectionDetailsProjection;
import com.bulka.eventservice.model.venue.Section;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SectionRepository extends JpaRepository<Section, UUID> {

    @Query("""
    select new com.bulka.eventservice.dto.projection.venue.SectionDetailsProjection(
        s.id,
        s.name,
        s.layout.x,
        s.layout.y,
        s.layout.width,
        s.layout.height,
        s.layout.rotation
    )
    from Section s
    where s.venue.id = :venueId
""")
    List<SectionDetailsProjection> findSectionsByVenueId(UUID venueId);

    List<Section> findAllByVenueId(UUID venueId);
    Optional<Section> findByIdAndVenueId(UUID id, UUID venueId);
}
