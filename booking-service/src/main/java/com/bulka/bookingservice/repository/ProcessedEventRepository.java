package com.bulka.bookingservice.repository;


import com.bulka.bookingservice.model.ProcessedEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProcessedEventRepository extends JpaRepository<ProcessedEvent, UUID> {

    @Modifying
    @Query(value = """
            INSERT INTO processed_events(event_id, processed_at)
            VALUES (:eventId, CURRENT_TIMESTAMP)
            ON CONFLICT (event_id) DO NOTHING
            """,
            nativeQuery = true
    )
    int markProcessed(UUID eventId);
}

