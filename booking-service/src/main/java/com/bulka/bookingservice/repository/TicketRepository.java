package com.bulka.bookingservice.repository;

import com.bulka.bookingservice.model.Ticket;
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
