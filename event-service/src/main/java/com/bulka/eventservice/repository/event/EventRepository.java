package com.bulka.eventservice.repository.event;

import com.bulka.eventservice.dto.projection.event.EventDetailsProjection;
import com.bulka.eventservice.model.event.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


import java.util.Optional;
import java.util.UUID;

@Repository
public interface EventRepository
        extends JpaRepository<Event, UUID>, JpaSpecificationExecutor<Event> {

    @Query("""
    select new com.bulka.eventservice.dto.projection.event.EventDetailsProjection(
        e.id,
        e.venue.id,
        e.venue.dimensions.width,
        e.venue.dimensions.height,
        e.name,
        e.description,
        e.startAt,
        e.endAt,
        e.status,
        e.eventType,
        e.createdAt,
        e.updatedAt
    )
    from Event e
    where e.id = :eventId
    """)
    Optional<EventDetailsProjection> findDetailsById(UUID eventId);
}
