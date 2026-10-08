package com.bulka.eventservice.repository.event;

import com.bulka.eventservice.dto.projection.event.EventSeatDetailsProjection;
import com.bulka.eventservice.dto.projection.internal.EventSeatDetailsInfoProjection;
import com.bulka.eventservice.dto.projection.internal.EventSeatInfoProjection;
import com.bulka.eventservice.model.event.EventSeat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Repository
public interface EventSeatRepository extends JpaRepository<EventSeat, UUID> {

    @Query("""
    select new com.bulka.eventservice.dto.projection.internal.EventSeatDetailsInfoProjection(
        es.eventSection.event.id,
        es.eventSection.event.name,
        es.eventSection.event.startAt,
        es.eventSection.event.endAt,
        es.eventSection.event.venue.name,
        es.id,
        es.eventSection.section.name,
        es.seat.rowNumber,
        es.seat.seatNumber
    )
    from EventSeat es
    where es.eventSection.event.id = :eventId
      and es.id in :eventSeatIds
""")
    List<EventSeatDetailsInfoProjection> findDetailsByEventIdAndIds(
            UUID eventId,
            Collection<UUID> eventSeatIds
    );

    @Query("""
    select new com.bulka.eventservice.dto.projection.internal.EventSeatInfoProjection(
        es.id,
        es.price,
        es.status
    )
    from EventSeat es
    where es.eventSection.event.id = :eventId
      and es.id in :eventSeatIds
    """)
    List<EventSeatInfoProjection> findInfoByEventIdAndIds(
            UUID eventId,
            Collection<UUID> eventSeatIds
    );

    @Query("""
    select new com.bulka.eventservice.dto.projection.event.EventSeatDetailsProjection(
        es.id,
        es.eventSection.id,
        es.seat.id,
        es.status,
        es.price,
        es.seat.rowNumber,
        es.seat.seatNumber,
        es.seat.layout.x,
        es.seat.layout.y,
        es.seat.layout.width,
        es.seat.layout.height,
        es.seat.layout.rotation
    )
    from EventSeat es
    where es.eventSection.id in :eventSectionIds
    """)
    List<EventSeatDetailsProjection> findDetailsByEventSectionIds(
            Collection<UUID> eventSectionIds
    );

    List<EventSeat> findAllByEventSectionIdIn(List<UUID> eventSectionIds);
    List<EventSeat> findAllByEventSectionEventIdAndIdIn(UUID eventId, List<UUID> ids);

    @Query("""
        SELECT es
        FROM EventSeat es
        JOIN FETCH es.eventSection esec
        JOIN FETCH es.seat s
        WHERE es.id IN :eventSeatIds
          AND esec.event.id = :eventId
        """)
    List<EventSeat> findAllByEventIdAndIds(
            @Param("eventId") UUID eventId,
            @Param("eventSeatIds") List<UUID> eventSeatIds
    );

    @Modifying
    @Query(value = """
        UPDATE event_seats es
        SET status = 'SOLD'
        FROM event_sections esec
        WHERE es.event_section_id = esec.id
          AND esec.event_id = :eventId
          AND es.id IN (:eventSeatIds)
          AND es.status = 'AVAILABLE'
        """, nativeQuery = true)
    int sellAvailableSeats(UUID eventId, List<UUID> eventSeatIds);

    @Modifying
    @Query(value = """
        UPDATE event_seats es
        SET status = 'AVAILABLE'
        FROM event_sections esec
        WHERE es.event_section_id = esec.id
          AND esec.event_id = :eventId
          AND es.id IN (:eventSeatIds)
          AND es.status = 'SOLD'
        """, nativeQuery = true)
    int cancelSellSeats(UUID eventId, List<UUID> eventSeatIds);
}
