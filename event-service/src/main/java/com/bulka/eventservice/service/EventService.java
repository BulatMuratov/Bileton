package com.bulka.eventservice.service;

import com.bulka.eventservice.dto.VenueSizeDto;
import com.bulka.eventservice.dto.request.EventDetailsRequestDto;
import com.bulka.eventservice.dto.request.EventFilterRequest;
import com.bulka.eventservice.dto.request.EventInfoRequestDto;
import com.bulka.eventservice.dto.request.EventSeatRequestDto;
import com.bulka.eventservice.dto.response.EventDetailsResponseDto;
import com.bulka.eventservice.dto.response.EventInfoResponseDto;
import com.bulka.eventservice.dto.response.EventSeatFullInfoResponseDto;
import com.bulka.eventservice.dto.response.EventSeatsFullInfoResponseDto;
import com.bulka.eventservice.exception.IllegalArgumentException;
import com.bulka.eventservice.exception.event.EventNotFoundException;
import com.bulka.eventservice.exception.event.EventSeatDataIntegrityException;
import com.bulka.eventservice.exception.event.InvalidEventStateException;
import com.bulka.eventservice.exception.venue.VenueNotFoundException;
import com.bulka.eventservice.mapper.EventMapper;
import com.bulka.eventservice.mapper.EventSeatMapper;
import com.bulka.eventservice.model.Event;
import com.bulka.eventservice.model.EventSeat;
import com.bulka.eventservice.model.EventSeatStatus;
import com.bulka.eventservice.model.EventStatus;
import com.bulka.eventservice.model.Seat;
import com.bulka.eventservice.model.Venue;
import com.bulka.eventservice.repository.EventRepository;
import com.bulka.eventservice.repository.EventSeatRepository;
import com.bulka.eventservice.repository.SeatRepository;
import com.bulka.eventservice.repository.VenueRepository;
import com.bulka.eventservice.repository.specification.EventSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final EventSeatRepository eventSeatRepository;
    private final VenueRepository venueRepository;
    private final SeatRepository seatRepository;

    private final EventMapper eventMapper;
    private final EventSeatMapper eventSeatMapper;

    @Transactional
    public EventDetailsResponseDto createEvent(EventDetailsRequestDto eventRequestDto) {
        Venue venue = venueRepository.findById(eventRequestDto.getVenueId())
                .orElseThrow(() -> new VenueNotFoundException("Venue not found"));

        List<UUID> seatIds = eventRequestDto.getSeats()
                .stream()
                .map(EventSeatRequestDto::getSeatId)
                .toList();

        if(seatIds.size() != seatIds.stream().distinct().count()){
            throw new IllegalArgumentException("The body contains duplicates");
        }
        Map<UUID, BigDecimal> seatPriceMap = eventRequestDto.getSeats()
                .stream()
                .collect(Collectors.toMap(EventSeatRequestDto::getSeatId, EventSeatRequestDto::getPrice));

        List<Seat> seats = seatRepository.findAllByVenueIdAndIdIn(venue.getId(), seatIds);

        if (seats.size() != eventRequestDto.getSeats().size()) {
            throw new IllegalArgumentException("Some seats do not belong to venue");
        }

        Event event = Event.builder()
                .venue(venue)
                .name(eventRequestDto.getName())
                .description(eventRequestDto.getDescription())
                .startAt(eventRequestDto.getStartAt())
                .endAt(eventRequestDto.getEndAt())
                .status(EventStatus.DRAFT)
                .build();

        Event savedEvent = eventRepository.save(event);

        List<EventSeat> eventSeatList = seats
                .stream()
                .map(seat -> EventSeat.builder()
                        .event(savedEvent)
                        .seat(seat)
                        .status(EventSeatStatus.AVAILABLE)
                        .price(seatPriceMap.get(seat.getId()))
                        .build())
                .toList();

        List<EventSeat> savedEventSeatList = eventSeatRepository.saveAll(eventSeatList);

        return eventMapper.toDetailsResponse(savedEvent, savedEventSeatList);
    }

    @Transactional(readOnly = true)
    public List<EventInfoResponseDto> getAllEvents() {
        List<Event> events = eventRepository.findAll();

        return events.stream()
                .map(eventMapper::toInfoResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<EventInfoResponseDto> getEvents(EventFilterRequest filter, Pageable pageable) {
        Specification<Event> specification = Specification.where((Specification<Event>) null);

        if(filter.getStatus() != null){
            specification = specification.and(EventSpecifications.hasStatus(filter.getStatus()));
        }
        if (filter.getVenueId() != null) {
            specification = specification.and(
                    EventSpecifications.hasVenueId(filter.getVenueId())
            );
        }

        if (filter.getFrom() != null) {
            specification = specification.and(
                    EventSpecifications.startAtAfterOrEqual(
                            filter.getFrom()
                    )
            );
        }

        if (filter.getTo() != null) {
            specification = specification.and(
                    EventSpecifications.startAtBeforeOrEqual(
                            filter.getTo()
                    )
            );
        }

        return eventRepository
                .findAll(specification, pageable)
                .map(eventMapper::toInfoResponse);
    }

    @Transactional(readOnly = true)
    public EventSeatsFullInfoResponseDto getEventSeatsByEventId(UUID eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Event not found"));

        Venue venue = event.getVenue();

        List<Seat> seatList = seatRepository.findAllByVenueId(venue.getId());
        List<EventSeat> eventSeatList = eventSeatRepository.findAllByEventId(eventId);

        Map<UUID, EventSeat> eventSeatMap = eventSeatList.stream()
                .collect(Collectors.toMap(
                        eventSeat -> eventSeat.getSeat().getId(),
                        Function.identity()
                ));

        List<EventSeatFullInfoResponseDto> seats = seatList.stream()
                .map(seat -> {
                    EventSeat eventSeat = eventSeatMap.get(seat.getId());

                    if (eventSeat == null) {
                        throw new EventSeatDataIntegrityException("EventSeat not found for seat: " + seat.getId());
                    }

                    return eventSeatMapper.toFullInfoResponse(seat, eventSeat);
                })
                .toList();

        return EventSeatsFullInfoResponseDto.builder()
                .eventId(event.getId())
                .venue(VenueSizeDto.builder()
                        .height(venue.getHeight())
                        .width(venue.getWidth())
                        .build())
                .seats(seats)
                .build();
    }

    @Transactional(readOnly = true)
    public EventInfoResponseDto getEventById(UUID eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Event not found"));
        return eventMapper.toInfoResponse(event);
    }

    @Transactional
    public EventInfoResponseDto publishEvent(UUID eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Event not found"));

        if (!EventStatus.DRAFT.equals(event.getStatus())) {
            throw new InvalidEventStateException("Event cannot be published from status: " + "event.getStatus()");
        }
        event.setStatus(EventStatus.PUBLISHED);

        return eventMapper.toInfoResponse(event);
    }

    @Transactional
    public EventInfoResponseDto cancelEvent(UUID eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Event not found"));

        if (event.getStatus() == EventStatus.CANCELLED || event.getStatus() == EventStatus.FINISHED) {
            throw new InvalidEventStateException("Event cannot be cancelled from status: "  + "event.getStatus()");
        }
        event.setStatus(EventStatus.CANCELLED);

        return eventMapper.toInfoResponse(event);
    }

    @Transactional
    public EventInfoResponseDto updateEvent(UUID eventId, EventInfoRequestDto eventInfoRequestDto) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Event not found"));

        if (eventInfoRequestDto.getName() != null) {
            event.setName(eventInfoRequestDto.getName());
        }
        if (eventInfoRequestDto.getDescription() != null) {
            event.setDescription(eventInfoRequestDto.getDescription());
        }
        if (eventInfoRequestDto.getStartAt() != null) {
            event.setStartAt(eventInfoRequestDto.getStartAt());
        }
        if (eventInfoRequestDto.getEndAt() != null) {
            event.setEndAt(eventInfoRequestDto.getEndAt());
        }

        return eventMapper.toInfoResponse(event);
    }

//    @Transactional
//    public void deleteEvent(UUID eventId) {
//        eventRepository.deleteById(eventId);
//    }
}

