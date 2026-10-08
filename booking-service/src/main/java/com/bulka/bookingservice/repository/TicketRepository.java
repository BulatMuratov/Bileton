package com.bulka.bookingservice.repository;

import com.bulka.bookingservice.dto.projection.TicketDetailsProjection;
import com.bulka.bookingservice.dto.projection.TicketSummaryProjection;
import com.bulka.bookingservice.model.ticket.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, UUID> {

    @Query("""
    select new com.bulka.bookingservice.dto.projection.TicketDetailsProjection(
        t.id,
        t.ticketNumber,
        t.booking.id,
        t.eventId,
        t.eventName,
        t.eventStartAt,
        t.eventEndAt,
        t.venueName,
        t.sectionName,
        t.rowNumber,
        t.seatNumber,
        t.price,
        t.currency,
        t.status,
        t.createdAt
    )
    from Ticket t
    where t.id = :ticketId
      and t.booking.userId = :userId
    """)
    Optional<TicketDetailsProjection> findDetailsByIdAndBookingUserId(
            UUID ticketId,
            UUID userId
    );

    @Query("""
    select new com.bulka.bookingservice.dto.projection.TicketSummaryProjection(
        t.id,
        t.ticketNumber,
        t.eventName,
        t.eventStartAt,
        t.eventEndAt,
        t.venueName,
        t.sectionName,
        t.rowNumber,
        t.seatNumber,
        t.price,
        t.status
    )
    from Ticket t
    where t.booking.userId = :userId
    """)
    List<TicketSummaryProjection> findAllSummaryByBookingUserId(UUID userId);


    Optional<Ticket> findByIdAndBookingUserId(UUID id, UUID userId);
    List<Ticket> findAllByBookingUserId(UUID userId);

    @Modifying
    @Query("""
        UPDATE Ticket t
        SET t.eventName = :name,
            t.eventStartAt = :startAt,
            t.eventEndAt = :endAt
        WHERE t.eventId = :eventId
        """)
    int updateEventData(
            @Param("eventId") UUID eventId,
            @Param("name") String name,
            @Param("startAt") OffsetDateTime startAt,
            @Param("endAt") OffsetDateTime endAt
    );
}
