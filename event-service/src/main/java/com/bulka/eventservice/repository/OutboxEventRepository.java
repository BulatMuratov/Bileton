package com.bulka.eventservice.repository;

import com.bulka.eventservice.model.outbox.OutboxEvent;
import com.bulka.eventservice.model.outbox.OutboxEventStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OutboxEventRepository extends JpaRepository<OutboxEvent, UUID> {

    List<OutboxEvent> findTop100ByStatusOrderByCreatedAtDesc(OutboxEventStatus status);
}
