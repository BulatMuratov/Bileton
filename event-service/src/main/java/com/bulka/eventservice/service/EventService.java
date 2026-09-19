package com.bulka.eventservice.service;

import com.bulka.eventservice.dto.VenueSizeDto;
import com.bulka.eventservice.dto.request.event.EventDetailsRequestDto;
import com.bulka.eventservice.dto.request.event.EventInfoRequestDto;
import com.bulka.eventservice.dto.request.event.EventSeatRequestDto;
import com.bulka.eventservice.dto.request.event.EventSectionRequestDto;
import com.bulka.eventservice.dto.response.event.EventDetailsResponseDto;
import com.bulka.eventservice.dto.response.event.EventSeatDetailsResponseDto;
import com.bulka.eventservice.dto.response.event.EventSectionDetailsResponseDto;
import com.bulka.eventservice.dto.response.event.EventSummaryResponseDto;
import com.bulka.eventservice.exception.venue.DuplicateSectionException;
import com.bulka.eventservice.exception.event.EventNotFoundException;
import com.bulka.eventservice.exception.event.InvalidEventStateException;
import com.bulka.eventservice.exception.venue.DuplicateSeatException;
import com.bulka.eventservice.exception.venue.SeatNotFoundException;
import com.bulka.eventservice.exception.venue.SectionNotFoundException;
import com.bulka.eventservice.exception.venue.VenueNotFoundException;
import com.bulka.eventservice.mapper.event.EventMapper;
import com.bulka.eventservice.mapper.event.EventSeatMapper;
import com.bulka.eventservice.mapper.event.EventSectionMapper;
import com.bulka.eventservice.model.event.Event;
import com.bulka.eventservice.model.event.EventSeat;
import com.bulka.eventservice.model.event.EventSeatStatus;
import com.bulka.eventservice.model.event.EventSection;
import com.bulka.eventservice.model.event.EventStatus;
import com.bulka.eventservice.model.venue.Seat;
import com.bulka.eventservice.model.venue.Section;
import com.bulka.eventservice.model.venue.Venue;
import com.bulka.eventservice.repository.event.EventRepository;
import com.bulka.eventservice.repository.event.EventSeatRepository;
import com.bulka.eventservice.repository.event.EventSectionRepository;
import com.bulka.eventservice.repository.venue.SeatRepository;
import com.bulka.eventservice.repository.venue.SectionRepository;
import com.bulka.eventservice.repository.venue.VenueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final EventSectionRepository eventSectionRepository;
    private final EventSeatRepository eventSeatRepository;

    private final VenueRepository venueRepository;
    private final SectionRepository sectionRepository;
    private final SeatRepository seatRepository;

    private final EventMapper eventMapper;
    private final EventSectionMapper eventSectionMapper;
    private final EventSeatMapper eventSeatMapper;

    @Transactional
    public EventDetailsResponseDto createEvent(EventDetailsRequestDto eventRequestDto) {
        validateRequest(eventRequestDto);
        Venue venue = venueRepository.findById(eventRequestDto.getVenueId())
                .orElseThrow(() -> new VenueNotFoundException("Venue not found"));

        Event event = eventMapper.toEntity(eventRequestDto, venue);
        Event savedEvent = eventRepository.save(event);

        List<EventSectionDetailsResponseDto> sections = eventRequestDto.getSections()
                .stream()
                .map(sectionDto -> createEventSection(savedEvent, sectionDto))
                .toList();

        VenueSizeDto venueSize = VenueSizeDto.builder()
                .width(venue.getWidth())
                .height(venue.getHeight())
                .build();

        return eventMapper.toDetailsResponse(savedEvent, sections, venueSize);

    }

    private EventSectionDetailsResponseDto createEventSection(
            Event event,
            EventSectionRequestDto request
    ) {
        Section section = sectionRepository.findByIdAndVenueId(request.getId(), event.getVenue().getId())
                .orElseThrow(() ->
                        new SectionNotFoundException("Section with id " + request.getId() + " not found")
                );

        EventSection eventSection = eventSectionMapper.toEntity(event, section);
        EventSection savedEventSection = eventSectionRepository.save(eventSection);

        List<Seat> seats = seatRepository.findAllBySectionId(section.getId());
        Map<UUID, Seat> seatsById = seats.stream()
                .collect(Collectors.toMap(
                        Seat::getId,
                        Function.identity()
                ));


        List<EventSeat> eventSeats = request.getSeats()
                .stream()
                .map(seatRequest -> {
                    Seat seat = seatsById.get(seatRequest.getSeatId());

                    if (seat == null) {
                        throw new SeatNotFoundException(
                                "Seat with id " + seatRequest.getSeatId() + " does not belong to section " + section.getId()
                        );
                    }

                    return EventSeat.builder()
                            .eventSection(savedEventSection)
                            .seat(seat)
                            .status(EventSeatStatus.AVAILABLE)
                            .price(seatRequest.getPrice())
                            .build();
                })
                .toList();

        List<EventSeat> savedEventSeats = eventSeatRepository.saveAll(eventSeats);

        List<EventSeatDetailsResponseDto> seatResponses = savedEventSeats.stream()
                .map(eventSeat -> eventSeatMapper.toDetailsResponse(eventSeat, eventSeat.getSeat()))
                .toList();

        return eventSectionMapper.toDetailsResponse(savedEventSection, section, seatResponses);
    }

    @Transactional(readOnly = true)
    public List<EventSummaryResponseDto> getEvents() {
        List<Event> events = eventRepository.findAll();

        return events.stream()
                .map(eventMapper::toSummaryResponse)
                .toList();
    }

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
    public EventSummaryResponseDto updateEvent(UUID eventId, EventInfoRequestDto eventInfoRequestDto) {
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

        return eventMapper.toSummaryResponse(event);
    }

    private void validateRequest(EventDetailsRequestDto request) {
        Set<UUID> sectionIds = request.getSections()
                .stream()
                .map(EventSectionRequestDto::getId)
                .collect(Collectors.toSet());

        if (sectionIds.size() != request.getSections().size()) {
            throw new DuplicateSectionException("Duplicate sections");
        }

        request.getSections().forEach(section -> {
            Set<UUID> seatIds = section.getSeats()
                    .stream()
                    .map(EventSeatRequestDto::getSeatId)
                    .collect(Collectors.toSet());

            if (seatIds.size() != section.getSeats().size()) {
                throw new DuplicateSeatException("Duplicate seats in section " + section.getId());
            }
        });
    }

//    @Transactional(readOnly = true)
//    public Page<EventInfoResponseDto> getEvents(EventFilterRequest filter, Pageable pageable) {
//        Specification<Event> specification = Specification.where((Specification<Event>) null);
//
//        if(filter.getStatus() != null){
//            specification = specification.and(EventSpecifications.hasStatus(filter.getStatus()));
//        }
//        if (filter.getVenueId() != null) {
//            specification = specification.and(
//                    EventSpecifications.hasVenueId(filter.getVenueId())
//            );
//        }
//
//        if (filter.getFrom() != null) {
//            specification = specification.and(
//                    EventSpecifications.startAtAfterOrEqual(
//                            filter.getFrom()
//                    )
//            );
//        }
//
//        if (filter.getTo() != null) {
//            specification = specification.and(
//                    EventSpecifications.startAtBeforeOrEqual(
//                            filter.getTo()
//                    )
//            );
//        }
//
//        return eventRepository
//                .findAll(specification, pageable)
//                .map(eventMapper::toInfoResponse);
//    }
//
//    @Transactional(readOnly = true)
//    public EventSeatsFullInfoResponseDto getEventSeatsByEventId(UUID eventId) {
//        Event event = eventRepository.findById(eventId)
//                .orElseThrow(() -> new EventNotFoundException("Event not found"));
//
//        Venue venue = event.getVenue();
//
//        List<Seat> seatList = seatRepository.findAllByVenueId(venue.getId());
//        List<EventSeat> eventSeatList = eventSeatRepository.findAllByEventId(eventId);
//
//        Map<UUID, EventSeat> eventSeatMap = eventSeatList.stream()
//                .collect(Collectors.toMap(
//                        eventSeat -> eventSeat.getSeat().getId(),
//                        Function.identity()
//                ));
//
//        List<EventSeatFullInfoResponseDto> seats = seatList.stream()
//                .map(seat -> {
//                    EventSeat eventSeat = eventSeatMap.get(seat.getId());
//
//                    if (eventSeat == null) {
//                        throw new EventSeatDataIntegrityException("EventSeat not found for seat: " + seat.getId());
//                    }
//
//                    return eventSeatMapper.toFullInfoResponse(seat, eventSeat);
//                })
//                .toList();
//
//        return EventSeatsFullInfoResponseDto.builder()
//                .eventId(event.getId())
//                .venue(VenueSizeDto.builder()
//                        .height(venue.getHeight())
//                        .width(venue.getWidth())
//                        .build())
//                .seats(seats)
//                .build();
//    }
//
    @Transactional
    public EventSummaryResponseDto publishEvent(UUID eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Event not found"));

        if (!EventStatus.DRAFT.equals(event.getStatus())) {
            throw new InvalidEventStateException("Event cannot be published from status: " + "event.getStatus()");
        }
        event.setStatus(EventStatus.PUBLISHED);

        return eventMapper.toSummaryResponse(event);
    }

    @Transactional
    public EventSummaryResponseDto cancelEvent(UUID eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException("Event not found"));

        if (event.getStatus() == EventStatus.CANCELLED || event.getStatus() == EventStatus.FINISHED) {
            throw new InvalidEventStateException("Event cannot be cancelled from status: "  + "event.getStatus()");
        }
        event.setStatus(EventStatus.CANCELLED);

        return eventMapper.toSummaryResponse(event);
    }

//    @Transactional
//    public void deleteEvent(UUID eventId) {
//        eventRepository.deleteById(eventId);
//    }
}

