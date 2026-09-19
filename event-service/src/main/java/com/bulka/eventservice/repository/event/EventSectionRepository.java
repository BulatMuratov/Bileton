package com.bulka.eventservice.repository.event;

import com.bulka.eventservice.model.event.EventSection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EventSectionRepository extends JpaRepository<EventSection, UUID> {
    List<EventSection> findAllByEventId(UUID eventId);
}
