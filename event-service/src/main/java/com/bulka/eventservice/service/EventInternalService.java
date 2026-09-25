package com.bulka.eventservice.service;

import com.bulka.eventservice.dto.internal.response.EventSeatDetailsInfoDto;
import com.bulka.eventservice.dto.response.event.VenueSizeDto;
import com.bulka.eventservice.dto.internal.response.EventSeatInfoDto;
import com.bulka.eventservice.dto.response.event.EventDetailsResponseDto;
import com.bulka.eventservice.dto.response.event.EventSeatDetailsResponseDto;
import com.bulka.eventservice.dto.response.event.EventSectionDetailsResponseDto;
import com.bulka.eventservice.exception.event.EventNotFoundException;
import com.bulka.eventservice.exception.event.EventSeatNotFoundException;
import com.bulka.eventservice.exception.event.SeatsNotAvailableException;
import com.bulka.eventservice.mapper.event.EventMapper;
import com.bulka.eventservice.mapper.event.EventSeatMapper;
import com.bulka.eventservice.mapper.event.EventSectionMapper;
import com.bulka.eventservice.model.event.Event;
import com.bulka.eventservice.model.event.EventSeat;
import com.bulka.eventservice.model.event.EventSection;
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
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() ->
                        new EventNotFoundException(
                                "Event with id " + eventId + " not found"
                        )
                );

        VenueSizeDto venueSize = VenueSizeDto.builder()
                .width(event.getVenue().getWidth())
                .height(event.getVenue().getHeight())
                .build();

        List<EventSection> eventSections =
                eventSectionRepository.findAllByEventId(eventId);

        if (eventSections.isEmpty()) {
            return eventMapper.toDetailsResponse(event, List.of(), venueSize);
        }

        List<UUID> eventSectionIds = eventSections.stream()
                .map(EventSection::getId)
                .toList();

        List<EventSeat> eventSeats =
                eventSeatRepository.findAllByEventSectionIdIn(eventSectionIds);

        Map<UUID, List<EventSeat>> seatsBySectionId =
                eventSeats.stream()
                        .collect(Collectors.groupingBy(
                                eventSeat -> eventSeat.getEventSection().getId()
                        ));

        List<EventSectionDetailsResponseDto> sectionResponses =
                eventSections.stream()
                        .map(eventSection -> {
                            List<EventSeat> seats =
                                    seatsBySectionId.getOrDefault(
                                            eventSection.getId(),
                                            List.of()
                                    );

                            List<EventSeatDetailsResponseDto> seatResponses =
                                    seats.stream()
                                            .map(eventSeat ->
                                                    eventSeatMapper.toDetailsResponse(
                                                            eventSeat,
                                                            eventSeat.getSeat()
                                                    )
                                            )
                                            .toList();

                            return eventSectionMapper.toDetailsResponse(
                                    eventSection,
                                    eventSection.getSection(),
                                    seatResponses
                            );
                        })
                        .toList();

        return eventMapper.toDetailsResponse(event, sectionResponses, venueSize);
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
//        return updated == eventSeatIds.size();
    }

    @Transactional
    public boolean cancelSellSeats(UUID eventId, List<UUID> eventSeatIds){
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
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException( "Event with id " + eventId + " not found" ) );

        List<EventSeat> eventSeats = eventSeatRepository.findAllByEventSectionEventIdAndIdIn(eventId, eventSeatIds);

        if(eventSeats.size() != eventSeatIds.size()) {
            throw new EventSeatNotFoundException("Some event seats do not belong to event " + eventId);
        }
        for(EventSeat eventSeat : eventSeats) {
            System.out.println(eventSeat.getStatus());
        }
        return eventSeats.stream()
                .map(eventSeat -> EventSeatInfoDto.builder()
                        .eventSeatId(eventSeat.getId())
                        .price(eventSeat.getPrice())
                        .status(eventSeat.getStatus())
                        .build())
                .toList();
    }

    @Transactional(readOnly = true)
    public List<EventSeatDetailsInfoDto> getEventSeatsDetails(UUID eventId, List<UUID> eventSeatIds) {
        List<EventSeat> eventSeats = eventSeatRepository.findAllByEventIdAndIds(eventId, eventSeatIds);
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Event with id " + eventId + " not found" ));

        return eventSeats.stream()
                .map(eventSeat -> EventSeatDetailsInfoDto.builder()
                        .eventId(event.getId())
                        .eventName(event.getName())
                        .eventStartAt(event.getStartAt())
                        .eventEndAt(event.getEndAt())
                        .venueName(event.getVenue().getName())
                        .eventSeatId(eventSeat.getId())
                        .sectionName(eventSeat.getEventSection().getSection().getName())
                        .rowNumber(eventSeat.getSeat().getRowNumber())
                        .seatNumber(eventSeat.getSeat().getSeatNumber())
                        .build())
                .toList();


    }
}
