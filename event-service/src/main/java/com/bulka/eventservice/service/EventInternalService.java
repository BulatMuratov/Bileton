package com.bulka.eventservice.service;

import com.bulka.eventservice.dto.projection.internal.EventSeatDetailsInfoProjection;
import com.bulka.eventservice.dto.response.internal.EventSeatDetailsInfoDto;
import com.bulka.eventservice.dto.projection.event.EventDetailsProjection;
import com.bulka.eventservice.dto.projection.event.EventSeatDetailsProjection;
import com.bulka.eventservice.dto.projection.internal.EventSeatInfoProjection;
import com.bulka.eventservice.dto.projection.event.EventSectionDetailsProjection;
import com.bulka.eventservice.dto.response.event.VenueSizeDto;
import com.bulka.eventservice.dto.response.internal.EventSeatInfoDto;
import com.bulka.eventservice.dto.response.event.EventDetailsResponseDto;
import com.bulka.eventservice.dto.response.event.EventSectionDetailsResponseDto;
import com.bulka.eventservice.exception.event.EventNotFoundException;
import com.bulka.eventservice.exception.event.EventSeatNotFoundException;
import com.bulka.eventservice.exception.event.SeatsNotAvailableException;
import com.bulka.eventservice.mapper.event.EventMapper;
import com.bulka.eventservice.mapper.event.EventSeatMapper;
import com.bulka.eventservice.mapper.event.EventSectionMapper;
import com.bulka.eventservice.model.event.Event;
import com.bulka.eventservice.model.event.EventSeat;
import com.bulka.eventservice.repository.event.EventRepository;
import com.bulka.eventservice.repository.event.EventSeatRepository;
import com.bulka.eventservice.repository.event.EventSectionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventInternalService {

    private final EventRepository eventRepository;
    private final EventSectionRepository eventSectionRepository;
    private final EventSeatRepository eventSeatRepository;

    private final EventMapper eventMapper;
    private final EventSectionMapper eventSectionMapper;
    private final EventSeatMapper eventSeatMapper;

    @Transactional(readOnly = true)
    public EventDetailsResponseDto getEventById(UUID eventId) {
        EventDetailsProjection event = eventRepository.findDetailsById(eventId)
                .orElseThrow(() ->
                        new EventNotFoundException(
                                "Event with id " + eventId + " not found"
                        )
                );

        List<EventSectionDetailsProjection> sections =
                eventSectionRepository.findDetailsByEventId(eventId);

        List<UUID> eventSectionIds = sections.stream()
                .map(EventSectionDetailsProjection::id)
                .toList();

        List<EventSeatDetailsProjection> seats =
                eventSeatRepository.findDetailsByEventSectionIds(eventSectionIds);

        Map<UUID, List<EventSeatDetailsProjection>> seatsBySection =
                seats.stream()
                        .collect(Collectors.groupingBy(
                                EventSeatDetailsProjection::eventSectionId
                        ));

        List<EventSectionDetailsResponseDto> sectionResponses =
                sections.stream()
                        .map(section ->
                                eventSectionMapper.toDetailsResponseFromProjection(
                                        section,
                                        seatsBySection.getOrDefault(
                                                        section.id(),
                                                        List.of()
                                                ).stream()
                                                .map(eventSeatMapper::toDetailsResponseFromProjection)
                                                .toList()
                                )
                        )
                        .toList();

        VenueSizeDto venueSize = VenueSizeDto.builder()
                .width(event.venueWidth())
                .height(event.venueHeight())
                .build();

        return eventMapper.toDetailsResponseFromProjection(
                event,
                sectionResponses,
                venueSize
        );
    }

    @Transactional
    public void sellSeats(UUID eventId, List<UUID> eventSeatIds){
        int updated = eventSeatRepository.sellAvailableSeats(
                eventId,
                eventSeatIds
        );

        if(updated != eventSeatIds.size()){
            throw new SeatsNotAvailableException("Not all seats are available");
        }
    }

    @Transactional
    public void cancelSellSeats(UUID eventId, List<UUID> eventSeatIds){
        int updated = eventSeatRepository.cancelSellSeats(
                eventId,
                eventSeatIds
        );

        if (updated != eventSeatIds.size()) {
            throw new IllegalStateException(
                    "Failed to cancel sale for all seats"
            );
        }
    }

    @Transactional(readOnly = true)
    public List<EventSeatInfoDto> getEventSeats(UUID eventId, List<UUID> eventSeatIds) {
        if (!eventRepository.existsById(eventId)) {
            throw new EventNotFoundException(
                    "Event with id " + eventId + " not found"
            );
        }

        List<EventSeatInfoProjection> seats =
                eventSeatRepository.findInfoByEventIdAndIds(
                        eventId,
                        eventSeatIds
                );

        if (seats.size() != eventSeatIds.size()) {
            throw new EventSeatNotFoundException(
                    "Some event seats do not belong to event " + eventId
            );
        }

        return seats.stream()
                .map(seat -> EventSeatInfoDto.builder()
                        .eventSeatId(seat.eventSeatId())
                        .price(seat.price())
                        .status(seat.status())
                        .build())
                .toList();
    }

    @Transactional(readOnly = true)
    public List<EventSeatDetailsInfoDto> getEventSeatsDetails(UUID eventId, List<UUID> eventSeatIds) {
        List<EventSeatDetailsInfoProjection> seats =
                eventSeatRepository.findDetailsByEventIdAndIds(
                        eventId,
                        eventSeatIds
                );

        if (seats.isEmpty()) {
            throw new EventNotFoundException(
                    "Event with id " + eventId + " not found"
            );
        }

        if (seats.size() != eventSeatIds.size()) {
            throw new EventSeatNotFoundException(
                    "Some event seats do not belong to event " + eventId
            );
        }

        return seats.stream()
                .map(seat -> EventSeatDetailsInfoDto.builder()
                        .eventId(seat.eventId())
                        .eventName(seat.eventName())
                        .eventStartAt(seat.eventStartAt())
                        .eventEndAt(seat.eventEndAt())
                        .venueName(seat.venueName())
                        .eventSeatId(seat.eventSeatId())
                        .sectionName(seat.sectionName())
                        .rowNumber(seat.rowNumber())
                        .seatNumber(seat.seatNumber())
                        .build())
                .toList();
    }
}
