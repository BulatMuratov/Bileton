package com.bulka.eventservice.repository.event;

import com.bulka.eventservice.dto.projection.event.EventSectionDetailsProjection;
import com.bulka.eventservice.model.event.EventSection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EventSectionRepository extends JpaRepository<EventSection, UUID> {

    @Query("""
    select new com.bulka.eventservice.dto.projection.event.EventSectionDetailsProjection(
        es.id,
        es.event.id,
        es.section.id,
        es.section.name,
        es.section.layout.x,
        es.section.layout.y,
        es.section.layout.width,
        es.section.layout.height,
        es.section.layout.rotation
    )
    from EventSection es
    where es.event.id = :eventId
    """)
    List<EventSectionDetailsProjection> findDetailsByEventId(UUID eventId);

    List<EventSection> findAllByEventId(UUID eventId);
}
