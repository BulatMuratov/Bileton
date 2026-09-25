package com.bulka.eventservice.repository.event;

import com.bulka.eventservice.model.event.EventSeat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EventSeatRepository extends JpaRepository<EventSeat, UUID> {
//    List<EventSeat> findAllByEventId(UUID eventId);
//    List<EventSeat> findAllByEventIdAndIdIn(UUID eventId, List<UUID> seatIds);
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
