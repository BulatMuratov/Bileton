package com.bulka.eventservice.repository.specification;

import com.bulka.eventservice.model.event.Event;
import com.bulka.eventservice.model.event.EventStatus;
import org.springframework.data.jpa.domain.Specification;

import java.time.OffsetDateTime;
import java.util.UUID;

public final class EventSpecifications {
    private EventSpecifications() {}

    public static Specification<Event> hasStatus(EventStatus status) {
        return (root, query, cb) ->
                cb.equal(root.get("status"), status);
    }

    public static Specification<Event> hasVenueId(UUID venueId) {
        return (root, query, cb) ->
                cb.equal(root.get("venue").get("id"), venueId);
    }

    public static Specification<Event> startAtAfterOrEqual(OffsetDateTime from) {
        return (root, query, cb) ->
                cb.greaterThanOrEqualTo(root.get("startAt"), from);
    }

    public static Specification<Event> startAtBeforeOrEqual(OffsetDateTime to) {
        return (root, query, cb) ->
                cb.lessThanOrEqualTo(root.get("startAt"), to);
    }
}
