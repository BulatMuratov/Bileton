package com.bulka.eventservice.service;

import com.bulka.eventservice.dto.request.event.EventDetailsRequestDto;
import com.bulka.eventservice.dto.request.event.EventFilterRequest;
import com.bulka.eventservice.dto.request.event.EventInfoRequestDto;
import com.bulka.eventservice.dto.request.event.EventSeatRequestDto;
import com.bulka.eventservice.dto.request.event.EventSectionRequestDto;
import com.bulka.eventservice.dto.response.event.EventDetailsResponseDto;
import com.bulka.eventservice.dto.response.event.EventSeatDetailsResponseDto;
import com.bulka.eventservice.dto.response.event.EventSectionDetailsResponseDto;
import com.bulka.eventservice.dto.response.event.EventSummaryResponseDto;
import com.bulka.eventservice.exception.event.EventNotFoundException;
import com.bulka.eventservice.exception.event.InvalidEventStateException;
import com.bulka.eventservice.exception.venue.SeatNotFoundException;
import com.bulka.eventservice.exception.venue.SectionNotFoundException;
import com.bulka.eventservice.exception.venue.VenueNotFoundException;
import com.bulka.eventservice.kafka.event.EventUpdated;
import com.bulka.eventservice.kafka.outbox.OutboxEventFactory;
import com.bulka.eventservice.mapper.event.EventMapper;
import com.bulka.eventservice.mapper.event.EventSeatMapper;
import com.bulka.eventservice.mapper.event.EventSectionMapper;
import com.bulka.eventservice.model.event.Event;
import com.bulka.eventservice.model.event.EventSeat;
import com.bulka.eventservice.model.event.EventSeatStatus;
import com.bulka.eventservice.model.event.EventSection;
import com.bulka.eventservice.model.event.EventStatus;
import com.bulka.eventservice.model.event.EventType;
import com.bulka.eventservice.model.idempotency.IdempotencyKey;
import com.bulka.eventservice.model.idempotency.IdempotencyOperation;
import com.bulka.eventservice.model.outbox.OutboxEvent;
import com.bulka.eventservice.model.venue.Seat;
import com.bulka.eventservice.model.venue.Section;
import com.bulka.eventservice.model.venue.Venue;
import com.bulka.eventservice.repository.IdempotencyKeyRepository;
import com.bulka.eventservice.repository.OutboxEventRepository;
import com.bulka.eventservice.repository.event.EventRepository;
import com.bulka.eventservice.repository.event.EventSeatRepository;
import com.bulka.eventservice.repository.event.EventSectionRepository;
import com.bulka.eventservice.repository.venue.SeatRepository;
import com.bulka.eventservice.repository.venue.SectionRepository;
import com.bulka.eventservice.repository.venue.VenueRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    EventRepository eventRepository;

    @Mock
    EventSectionRepository eventSectionRepository;

    @Mock
    EventSeatRepository eventSeatRepository;

    @Mock
    VenueRepository venueRepository;

    @Mock
    SectionRepository sectionRepository;

    @Mock
    SeatRepository seatRepository;

    @Mock
    IdempotencyKeyRepository idempotencyKeyRepository;

    @Mock
    OutboxEventRepository outboxEventRepository;

    @Mock
    OutboxEventFactory outboxEventFactory;

    private EventMapper eventMapper;
    private EventSectionMapper eventSectionMapper;
    private EventSeatMapper eventSeatMapper;

    EventService eventService;

    @BeforeEach
    void setUp() {
        eventMapper = new EventMapper();
        eventSectionMapper = new EventSectionMapper();
        eventSeatMapper = new EventSeatMapper();

        eventService = new EventService(
                eventRepository,
                eventSectionRepository,
                eventSeatRepository,
                venueRepository,
                sectionRepository,
                seatRepository,
                idempotencyKeyRepository,
                outboxEventRepository,
                eventMapper,
                eventSectionMapper,
                eventSeatMapper,
                outboxEventFactory
        );
    }

    @Test
    void createEvent_shouldCreateEventSuccessfully() {
        // given
        UUID venueId = UUID.randomUUID();
        UUID sectionId = UUID.randomUUID();
        UUID seatId = UUID.randomUUID();

        String idempotencyKey = "create-event-key";

        EventDetailsRequestDto request =
                createEventRequest(venueId, sectionId, seatId);

        Venue venue = createVenueEntity(venueId);
        Section section = createSectionEntity(sectionId, venue);
        Seat seat = createSeatEntity(seatId, section);

        when(idempotencyKeyRepository.insertIfAbsent(
                any(UUID.class),
                eq(idempotencyKey),
                eq(IdempotencyOperation.CREATE_EVENT.name()),
                any(UUID.class)
        )).thenReturn(1);

        when(venueRepository.findById(venueId))
                .thenReturn(Optional.of(venue));

        when(eventRepository.save(any(Event.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(sectionRepository.findByIdAndVenueId(sectionId, venueId))
                .thenReturn(Optional.of(section));

        when(eventSectionRepository.save(any(EventSection.class)))
                .thenAnswer(invocation -> {
                    EventSection eventSection = invocation.getArgument(0);
                    eventSection.setId(UUID.randomUUID());
                    return eventSection;
                });

        when(seatRepository.findAllBySectionId(sectionId))
                .thenReturn(List.of(seat));

        when(eventSeatRepository.saveAll(anyList()))
                .thenAnswer(invocation -> {
                    List<EventSeat> eventSeats = invocation.getArgument(0);

                    eventSeats.forEach(eventSeat ->
                            eventSeat.setId(UUID.randomUUID())
                    );

                    return eventSeats;
                });

        // when
        EventDetailsResponseDto result =
                eventService.createEvent(request, idempotencyKey);

        // then
        assertNotNull(result);

        verify(idempotencyKeyRepository).insertIfAbsent(
                any(UUID.class),
                eq(idempotencyKey),
                eq(IdempotencyOperation.CREATE_EVENT.name()),
                any(UUID.class)
        );

        verify(venueRepository).findById(venueId);

        verify(eventRepository).save(any(Event.class));

        verify(sectionRepository).findByIdAndVenueId(
                sectionId,
                venueId
        );

        verify(eventSectionRepository).save(any(EventSection.class));

        verify(seatRepository).findAllBySectionId(sectionId);

        verify(eventSeatRepository).saveAll(anyList());
    }

    private EventDetailsRequestDto createEventRequest(
            UUID venueId,
            UUID sectionId,
            UUID seatId
    ) {
        return EventDetailsRequestDto.builder()
                .venueId(venueId)
                .name("Rock Concert")
                .description("Rock concert description")
                .startAt(OffsetDateTime.parse("2026-10-01T19:00:00+03:00"))
                .endAt(OffsetDateTime.parse("2026-10-01T22:00:00+03:00"))
                .eventType(EventType.CONCERT)
                .sections(List.of(
                        EventSectionRequestDto.builder()
                                .id(sectionId)
                                .seats(List.of(
                                        EventSeatRequestDto.builder()
                                                .seatId(seatId)
                                                .price(new BigDecimal("2500.00"))
                                                .build()
                                ))
                                .build()
                ))
                .build();
    }

    @Test
    void createEvent_whenIdempotencyKeyAlreadyExists_shouldReturnExistingEvent() {
        // given
        UUID eventId = UUID.randomUUID();
        UUID venueId = UUID.randomUUID();
        UUID sectionId = UUID.randomUUID();
        UUID seatId = UUID.randomUUID();

        String idempotencyKey = "create-event-key";

        EventDetailsRequestDto request =
                createEventRequest(venueId, sectionId, seatId);

        Venue venue = createVenueEntity(venueId);
        Section section = createSectionEntity(sectionId, venue);
        Seat seat = createSeatEntity(seatId, section);

        Event event = Event.builder()
                .id(eventId)
                .venue(venue)
                .name("Rock Concert")
                .description("Rock concert description")
                .startAt(OffsetDateTime.parse("2026-10-01T19:00:00+03:00"))
                .endAt(OffsetDateTime.parse("2026-10-01T22:00:00+03:00"))
                .status(EventStatus.DRAFT)
                .eventType(EventType.CONCERT)
                .build();

        EventSection eventSection = EventSection.builder()
                .id(UUID.randomUUID())
                .event(event)
                .section(section)
                .build();

        EventSeat eventSeat = EventSeat.builder()
                .id(UUID.randomUUID())
                .eventSection(eventSection)
                .seat(seat)
                .status(EventSeatStatus.AVAILABLE)
                .price(new BigDecimal("2500.00"))
                .build();

        IdempotencyKey existingKey = IdempotencyKey.builder()
                .resourceId(eventId)
                .build();

        when(idempotencyKeyRepository.insertIfAbsent(
                any(UUID.class),
                eq(idempotencyKey),
                eq(IdempotencyOperation.CREATE_EVENT.name()),
                any(UUID.class)
        )).thenReturn(0);

        when(idempotencyKeyRepository.findByKeyAndOperation(
                idempotencyKey,
                IdempotencyOperation.CREATE_EVENT
        )).thenReturn(Optional.of(existingKey));

        when(eventRepository.findById(eventId))
                .thenReturn(Optional.of(event));

        when(eventSectionRepository.findAllByEventId(eventId))
                .thenReturn(List.of(eventSection));

        when(eventSeatRepository.findAllByEventSectionIdIn(
                List.of(eventSection.getId())
        )).thenReturn(List.of(eventSeat));

        // when
        EventDetailsResponseDto result =
                eventService.createEvent(request, idempotencyKey);

        // then
        assertNotNull(result);
        assertEquals(eventId, result.getId());

        verify(idempotencyKeyRepository).insertIfAbsent(
                any(UUID.class),
                eq(idempotencyKey),
                eq(IdempotencyOperation.CREATE_EVENT.name()),
                any(UUID.class)
        );

        verify(idempotencyKeyRepository).findByKeyAndOperation(
                idempotencyKey,
                IdempotencyOperation.CREATE_EVENT
        );

        verify(eventRepository).findById(eventId);

        verify(eventRepository, never()).save(any(Event.class));
        verify(eventSectionRepository, never()).save(any(EventSection.class));
        verify(eventSeatRepository, never()).saveAll(anyList());

        verify(sectionRepository, never())
                .findByIdAndVenueId(any(UUID.class), any(UUID.class));

        verify(seatRepository, never())
                .findAllBySectionId(any(UUID.class));
    }


    @Test
    void createEvent_whenIdempotencyRecordNotFound_shouldThrowException() {
        // given
        UUID venueId = UUID.randomUUID();
        UUID sectionId = UUID.randomUUID();
        UUID seatId = UUID.randomUUID();

        String idempotencyKey = "create-event-key";

        EventDetailsRequestDto request =
                createEventRequest(venueId, sectionId, seatId);

        when(idempotencyKeyRepository.insertIfAbsent(
                any(UUID.class),
                eq(idempotencyKey),
                eq(IdempotencyOperation.CREATE_EVENT.name()),
                any(UUID.class)
        )).thenReturn(0);

        when(idempotencyKeyRepository.findByKeyAndOperation(
                idempotencyKey,
                IdempotencyOperation.CREATE_EVENT
        )).thenReturn(Optional.empty());

        // when / then
        assertThrows(
                IllegalStateException.class,
                () -> eventService.createEvent(request, idempotencyKey)
        );

        verify(venueRepository, never()).findById(any(UUID.class));
        verify(eventRepository, never()).save(any(Event.class));
        verify(eventSectionRepository, never()).save(any(EventSection.class));
        verify(eventSeatRepository, never()).saveAll(anyList());
    }

    @Test
    void createEvent_whenVenueNotFound_shouldThrowException() {
        // given
        UUID venueId = UUID.randomUUID();
        UUID sectionId = UUID.randomUUID();
        UUID seatId = UUID.randomUUID();

        String idempotencyKey = "create-event-key";

        EventDetailsRequestDto request =
                createEventRequest(venueId, sectionId, seatId);

        when(idempotencyKeyRepository.insertIfAbsent(
                any(UUID.class),
                eq(idempotencyKey),
                eq(IdempotencyOperation.CREATE_EVENT.name()),
                any(UUID.class)
        )).thenReturn(1);

        when(venueRepository.findById(venueId))
                .thenReturn(Optional.empty());

        // when / then
        assertThrows(
                VenueNotFoundException.class,
                () -> eventService.createEvent(request, idempotencyKey)
        );

        verify(venueRepository).findById(venueId);

        verify(eventRepository, never()).save(any(Event.class));
        verify(eventSectionRepository, never()).save(any(EventSection.class));
        verify(eventSeatRepository, never()).saveAll(anyList());
        verify(sectionRepository, never())
                .findByIdAndVenueId(any(UUID.class), any(UUID.class));
    }

    @Test
    void createEvent_whenSectionNotFound_shouldThrowException() {
        // given
        UUID venueId = UUID.randomUUID();
        UUID sectionId = UUID.randomUUID();
        UUID seatId = UUID.randomUUID();

        String idempotencyKey = "create-event-key";

        EventDetailsRequestDto request =
                createEventRequest(venueId, sectionId, seatId);

        Venue venue = createVenueEntity(venueId);

        when(idempotencyKeyRepository.insertIfAbsent(
                any(UUID.class),
                eq(idempotencyKey),
                eq(IdempotencyOperation.CREATE_EVENT.name()),
                any(UUID.class)
        )).thenReturn(1);

        when(venueRepository.findById(venueId))
                .thenReturn(Optional.of(venue));

        when(eventRepository.save(any(Event.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(sectionRepository.findByIdAndVenueId(sectionId, venueId))
                .thenReturn(Optional.empty());

        // when / then
        assertThrows(
                SectionNotFoundException.class,
                () -> eventService.createEvent(request, idempotencyKey)
        );

        verify(eventRepository).save(any(Event.class));
        verify(sectionRepository).findByIdAndVenueId(sectionId, venueId);

        verify(eventSectionRepository, never())
                .save(any(EventSection.class));

        verify(seatRepository, never())
                .findAllBySectionId(any(UUID.class));

        verify(eventSeatRepository, never())
                .saveAll(anyList());
    }

    @Test
    void createEvent_whenSeatNotFound_shouldThrowException() {
        // given
        UUID venueId = UUID.randomUUID();
        UUID sectionId = UUID.randomUUID();
        UUID requestedSeatId = UUID.randomUUID();

        UUID anotherSeatId = UUID.randomUUID();

        String idempotencyKey = "create-event-key";

        EventDetailsRequestDto request =
                createEventRequest(venueId, sectionId, requestedSeatId);

        Venue venue = createVenueEntity(venueId);
        Section section = createSectionEntity(sectionId, venue);

        Seat anotherSeat = createSeatEntity(anotherSeatId, section);

        when(idempotencyKeyRepository.insertIfAbsent(
                any(UUID.class),
                eq(idempotencyKey),
                eq(IdempotencyOperation.CREATE_EVENT.name()),
                any(UUID.class)
        )).thenReturn(1);

        when(venueRepository.findById(venueId))
                .thenReturn(Optional.of(venue));

        when(eventRepository.save(any(Event.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(sectionRepository.findByIdAndVenueId(sectionId, venueId))
                .thenReturn(Optional.of(section));

        when(eventSectionRepository.save(any(EventSection.class)))
                .thenAnswer(invocation -> {
                    EventSection eventSection = invocation.getArgument(0);
                    eventSection.setId(UUID.randomUUID());
                    return eventSection;
                });

        when(seatRepository.findAllBySectionId(sectionId))
                .thenReturn(List.of(anotherSeat));

        // when / then
        assertThrows(
                SeatNotFoundException.class,
                () -> eventService.createEvent(request, idempotencyKey)
        );

        verify(seatRepository).findAllBySectionId(sectionId);

        verify(eventSeatRepository, never())
                .saveAll(anyList());
    }

    @Test
    void createEvent_whenMultipleSections_shouldCreateAllSections() {
        // given
        UUID venueId = UUID.randomUUID();

        UUID firstSectionId = UUID.randomUUID();
        UUID secondSectionId = UUID.randomUUID();

        UUID firstSeatId = UUID.randomUUID();
        UUID secondSeatId = UUID.randomUUID();

        String idempotencyKey = "create-event-key";

        EventSectionRequestDto firstSectionRequest =
                EventSectionRequestDto.builder()
                        .id(firstSectionId)
                        .seats(List.of(
                                EventSeatRequestDto.builder()
                                        .seatId(firstSeatId)
                                        .price(new BigDecimal("2500.00"))
                                        .build()
                        ))
                        .build();

        EventSectionRequestDto secondSectionRequest =
                EventSectionRequestDto.builder()
                        .id(secondSectionId)
                        .seats(List.of(
                                EventSeatRequestDto.builder()
                                        .seatId(secondSeatId)
                                        .price(new BigDecimal("3500.00"))
                                        .build()
                        ))
                        .build();

        EventDetailsRequestDto request =
                EventDetailsRequestDto.builder()
                        .venueId(venueId)
                        .name("Rock Concert")
                        .description("Rock concert description")
                        .startAt(OffsetDateTime.parse("2026-10-01T19:00:00+03:00"))
                        .endAt(OffsetDateTime.parse("2026-10-01T22:00:00+03:00"))
                        .eventType(EventType.CONCERT)
                        .sections(List.of(
                                firstSectionRequest,
                                secondSectionRequest
                        ))
                        .build();

        Venue venue = createVenueEntity(venueId);

        Section firstSection =
                createSectionEntity(firstSectionId, venue);

        Section secondSection =
                createSectionEntity(secondSectionId, venue);

        Seat firstSeat =
                createSeatEntity(firstSeatId, firstSection);

        Seat secondSeat =
                createSeatEntity(secondSeatId, secondSection);

        when(idempotencyKeyRepository.insertIfAbsent(
                any(UUID.class),
                eq(idempotencyKey),
                eq(IdempotencyOperation.CREATE_EVENT.name()),
                any(UUID.class)
        )).thenReturn(1);

        when(venueRepository.findById(venueId))
                .thenReturn(Optional.of(venue));

        when(eventRepository.save(any(Event.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(sectionRepository.findByIdAndVenueId(firstSectionId, venueId))
                .thenReturn(Optional.of(firstSection));

        when(sectionRepository.findByIdAndVenueId(secondSectionId, venueId))
                .thenReturn(Optional.of(secondSection));

        when(eventSectionRepository.save(any(EventSection.class)))
                .thenAnswer(invocation -> {
                    EventSection eventSection = invocation.getArgument(0);
                    eventSection.setId(UUID.randomUUID());
                    return eventSection;
                });

        when(seatRepository.findAllBySectionId(firstSectionId))
                .thenReturn(List.of(firstSeat));

        when(seatRepository.findAllBySectionId(secondSectionId))
                .thenReturn(List.of(secondSeat));

        when(eventSeatRepository.saveAll(anyList()))
                .thenAnswer(invocation -> {
                    List<EventSeat> eventSeats = invocation.getArgument(0);

                    eventSeats.forEach(eventSeat ->
                            eventSeat.setId(UUID.randomUUID())
                    );

                    return eventSeats;
                });

        // when
        EventDetailsResponseDto result =
                eventService.createEvent(request, idempotencyKey);

        // then
        assertNotNull(result);
        assertEquals(2, result.getSections().size());

        verify(sectionRepository).findByIdAndVenueId(
                firstSectionId,
                venueId
        );

        verify(sectionRepository).findByIdAndVenueId(
                secondSectionId,
                venueId
        );

        verify(seatRepository).findAllBySectionId(firstSectionId);
        verify(seatRepository).findAllBySectionId(secondSectionId);

        verify(eventSectionRepository, times(2))
                .save(any(EventSection.class));

        verify(eventSeatRepository, times(2))
                .saveAll(anyList());
    }

    @Test
    void createEvent_whenEventSaveFails_shouldThrowException() {
        // given
        UUID venueId = UUID.randomUUID();
        UUID sectionId = UUID.randomUUID();
        UUID seatId = UUID.randomUUID();

        String idempotencyKey = "create-event-key";

        EventDetailsRequestDto request =
                createEventRequest(venueId, sectionId, seatId);

        Venue venue = createVenueEntity(venueId);

        RuntimeException exception = new RuntimeException("Database error");

        when(idempotencyKeyRepository.insertIfAbsent(
                any(UUID.class),
                eq(idempotencyKey),
                eq(IdempotencyOperation.CREATE_EVENT.name()),
                any(UUID.class)
        )).thenReturn(1);

        when(venueRepository.findById(venueId))
                .thenReturn(Optional.of(venue));

        when(eventRepository.save(any(Event.class)))
                .thenThrow(exception);

        // when / then
        RuntimeException result = assertThrows(
                RuntimeException.class,
                () -> eventService.createEvent(request, idempotencyKey)
        );

        assertSame(exception, result);

        verify(eventRepository).save(any(Event.class));

        verify(sectionRepository, never())
                .findByIdAndVenueId(any(UUID.class), any(UUID.class));

        verify(eventSectionRepository, never())
                .save(any(EventSection.class));

        verify(seatRepository, never())
                .findAllBySectionId(any(UUID.class));

        verify(eventSeatRepository, never())
                .saveAll(anyList());
    }

    @Test
    void getEvents_withoutFilters_shouldReturnAllEvents() {
        // given
        EventFilterRequest filter = new EventFilterRequest();

        Event event1 = createEventEntity(UUID.randomUUID());
        Event event2 = createEventEntity(UUID.randomUUID());

        when(eventRepository.findAll(any(Specification.class)))
                .thenReturn(List.of(event1, event2));

        // when
        List<EventSummaryResponseDto> result =
                eventService.getEvents(filter, Pageable.unpaged());

        // then
        assertEquals(2, result.size());

        verify(eventRepository).findAll(any(Specification.class));
    }


    @Test
    void getEvents_whenFromProvided_shouldApplyStartDateFilter() {
        // given
        OffsetDateTime from =
                OffsetDateTime.parse("2026-10-01T00:00:00+03:00");

        EventFilterRequest filter = new EventFilterRequest();
        filter.setFrom(from);

        when(eventRepository.findAll(any(Specification.class)))
                .thenReturn(List.of());

        // when
        List<EventSummaryResponseDto> result =
                eventService.getEvents(filter, Pageable.unpaged());

        // then
        assertTrue(result.isEmpty());

        verify(eventRepository).findAll(any(Specification.class));
    }

    @Test
    void getEvents_whenToProvided_shouldApplyEndDateFilter() {
        // given
        OffsetDateTime to =
                OffsetDateTime.parse("2026-10-31T23:59:59+03:00");

        EventFilterRequest filter = new EventFilterRequest();
        filter.setTo(to);

        when(eventRepository.findAll(any(Specification.class)))
                .thenReturn(List.of());

        // when
        List<EventSummaryResponseDto> result =
                eventService.getEvents(filter, Pageable.unpaged());

        // then
        assertTrue(result.isEmpty());

        verify(eventRepository).findAll(any(Specification.class));
    }

    @Test
    void getEvents_whenEventTypeProvided_shouldApplyEventTypeFilter() {
        // given
        EventFilterRequest filter = new EventFilterRequest();
        filter.setEventType(EventType.CONCERT);

        when(eventRepository.findAll(any(Specification.class)))
                .thenReturn(List.of());

        // when
        List<EventSummaryResponseDto> result =
                eventService.getEvents(filter, Pageable.unpaged());

        // then
        assertTrue(result.isEmpty());

        verify(eventRepository).findAll(any(Specification.class));
    }

    @Test
    void getEvents_whenStatusProvided_shouldApplyStatusFilter() {
        // given
        EventFilterRequest filter = new EventFilterRequest();
        filter.setStatus(EventStatus.PUBLISHED);

        when(eventRepository.findAll(any(Specification.class)))
                .thenReturn(List.of());

        // when
        List<EventSummaryResponseDto> result =
                eventService.getEvents(filter, Pageable.unpaged());

        // then
        assertTrue(result.isEmpty());

        verify(eventRepository).findAll(any(Specification.class));
    }

    @Test
    void getEvents_whenVenueIdProvided_shouldApplyVenueFilter() {
        // given
        UUID venueId = UUID.randomUUID();

        EventFilterRequest filter = new EventFilterRequest();
        filter.setVenueId(venueId);

        when(eventRepository.findAll(any(Specification.class)))
                .thenReturn(List.of());

        // when
        List<EventSummaryResponseDto> result =
                eventService.getEvents(filter, Pageable.unpaged());

        // then
        assertTrue(result.isEmpty());

        verify(eventRepository).findAll(any(Specification.class));
    }

    @Test
    void getEvents_whenAllFiltersProvided_shouldApplyAllFilters() {
        // given
        OffsetDateTime from =
                OffsetDateTime.parse("2026-10-01T00:00:00+03:00");

        OffsetDateTime to =
                OffsetDateTime.parse("2026-10-31T23:59:59+03:00");

        UUID venueId = UUID.randomUUID();

        EventFilterRequest filter = new EventFilterRequest();
        filter.setFrom(from);
        filter.setTo(to);
        filter.setEventType(EventType.CONCERT);
        filter.setStatus(EventStatus.PUBLISHED);
        filter.setVenueId(venueId);

        Event event = createEventEntity(UUID.randomUUID());

        when(eventRepository.findAll(any(Specification.class)))
                .thenReturn(List.of(event));

        // when
        List<EventSummaryResponseDto> result =
                eventService.getEvents(filter, Pageable.unpaged());

        // then
        assertEquals(1, result.size());

        verify(eventRepository).findAll(any(Specification.class));
    }

    @Test
    void getEvents_whenNoEventsFound_shouldReturnEmptyList() {
        // given
        EventFilterRequest filter = new EventFilterRequest();

        when(eventRepository.findAll(any(Specification.class)))
                .thenReturn(List.of());

        // when
        List<EventSummaryResponseDto> result =
                eventService.getEvents(filter, Pageable.unpaged());

        // then
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(eventRepository).findAll(any(Specification.class));
    }

    @Test
    void getEvents_whenRepositoryFails_shouldThrowException() {
        // given
        EventFilterRequest filter = new EventFilterRequest();

        RuntimeException exception =
                new RuntimeException("Database error");

        when(eventRepository.findAll(any(Specification.class)))
                .thenThrow(exception);

        // when / then
        RuntimeException result = assertThrows(
                RuntimeException.class,
                () -> eventService.getEvents(filter, Pageable.unpaged())
        );

        assertSame(exception, result);

        verify(eventRepository).findAll(any(Specification.class));
    }

    @Test
    void getEventById_shouldReturnEventWithSectionsAndSeats() {
        // given
        UUID eventId = UUID.randomUUID();
        UUID sectionId = UUID.randomUUID();
        UUID seatId = UUID.randomUUID();

        Event event = createEventEntity(eventId);
        Section section = createSectionEntity(sectionId, event.getVenue());
        Seat seat = createSeatEntity(seatId, section);

        EventSection eventSection = EventSection.builder()
                .id(UUID.randomUUID())
                .event(event)
                .section(section)
                .build();

        EventSeat eventSeat = EventSeat.builder()
                .id(UUID.randomUUID())
                .eventSection(eventSection)
                .seat(seat)
                .status(EventSeatStatus.AVAILABLE)
                .price(new BigDecimal("2500.00"))
                .build();

        when(eventRepository.findById(eventId))
                .thenReturn(Optional.of(event));

        when(eventSectionRepository.findAllByEventId(eventId))
                .thenReturn(List.of(eventSection));

        when(eventSeatRepository.findAllByEventSectionIdIn(List.of(eventSection.getId())))
                .thenReturn(List.of(eventSeat));

        // when
        EventDetailsResponseDto result = eventService.getEventById(eventId);

        // then
        assertEquals(eventId, result.getId());
        assertEquals(event.getName(), result.getName());
        assertEquals(event.getVenue().getId(), result.getVenueId());

        assertEquals(1, result.getSections().size());

        EventSectionDetailsResponseDto sectionResponse = result.getSections().get(0);

        assertEquals(eventSection.getId(), sectionResponse.getId());
        assertEquals(sectionId, sectionResponse.getSectionId());
        assertEquals(1, sectionResponse.getSeats().size());

        EventSeatDetailsResponseDto seatResponse = sectionResponse.getSeats().get(0);

        assertEquals(eventSeat.getId(), seatResponse.getId());
        assertEquals(seatId, seatResponse.getSeatId());
        assertEquals(EventSeatStatus.AVAILABLE, seatResponse.getStatus());
        assertEquals(new BigDecimal("2500.00"), seatResponse.getPrice());

        verify(eventRepository).findById(eventId);
        verify(eventSectionRepository).findAllByEventId(eventId);
        verify(eventSeatRepository)
                .findAllByEventSectionIdIn(List.of(eventSection.getId()));
    }

    @Test
    void getEventById_whenEventNotFound_shouldThrowException() {
        // given
        UUID eventId = UUID.randomUUID();

        when(eventRepository.findById(eventId))
                .thenReturn(Optional.empty());

        // when / then
        assertThrows(
                EventNotFoundException.class,
                () -> eventService.getEventById(eventId)
        );

        verify(eventRepository).findById(eventId);
        verifyNoInteractions(eventSectionRepository, eventSeatRepository);
    }

    @Test
    void getEventById_whenEventHasNoSections_shouldReturnEmptySections() {
        // given
        UUID eventId = UUID.randomUUID();

        Event event = createEventEntity(eventId);

        when(eventRepository.findById(eventId))
                .thenReturn(Optional.of(event));

        when(eventSectionRepository.findAllByEventId(eventId))
                .thenReturn(List.of());

        when(eventSeatRepository.findAllByEventSectionIdIn(List.of()))
                .thenReturn(List.of());

        // when
        EventDetailsResponseDto result = eventService.getEventById(eventId);

        // then
        assertEquals(eventId, result.getId());
        assertNotNull(result.getSections());
        assertTrue(result.getSections().isEmpty());

        verify(eventRepository).findById(eventId);
        verify(eventSectionRepository).findAllByEventId(eventId);
        verify(eventSeatRepository).findAllByEventSectionIdIn(List.of());
    }

    @Test
    void getEventById_whenSectionsHaveNoSeats_shouldReturnEmptySeatLists() {
        // given
        UUID eventId = UUID.randomUUID();
        UUID sectionId = UUID.randomUUID();

        Event event = createEventEntity(eventId);
        Section section = createSectionEntity(sectionId, event.getVenue());

        EventSection eventSection = EventSection.builder()
                .id(UUID.randomUUID())
                .event(event)
                .section(section)
                .build();

        when(eventRepository.findById(eventId))
                .thenReturn(Optional.of(event));

        when(eventSectionRepository.findAllByEventId(eventId))
                .thenReturn(List.of(eventSection));

        when(eventSeatRepository.findAllByEventSectionIdIn(List.of(eventSection.getId())))
                .thenReturn(List.of());

        // when
        EventDetailsResponseDto result = eventService.getEventById(eventId);

        // then
        assertEquals(1, result.getSections().size());

        EventSectionDetailsResponseDto sectionResponse =
                result.getSections().get(0);

        assertEquals(eventSection.getId(), sectionResponse.getId());
        assertNotNull(sectionResponse.getSeats());
        assertTrue(sectionResponse.getSeats().isEmpty());

        verify(eventSeatRepository)
                .findAllByEventSectionIdIn(List.of(eventSection.getId()));
    }

    @Test
    void getEventById_shouldGroupSeatsBySection() {
        // given
        UUID eventId = UUID.randomUUID();

        Event event = createEventEntity(eventId);

        UUID sectionId1 = UUID.randomUUID();
        UUID sectionId2 = UUID.randomUUID();

        Section section1 = createSectionEntity(sectionId1, event.getVenue());
        Section section2 = createSectionEntity(sectionId2, event.getVenue());

        EventSection eventSection1 = EventSection.builder()
                .id(UUID.randomUUID())
                .event(event)
                .section(section1)
                .build();

        EventSection eventSection2 = EventSection.builder()
                .id(UUID.randomUUID())
                .event(event)
                .section(section2)
                .build();

        Seat seat1 = createSeatEntity(UUID.randomUUID(), section1);
        Seat seat2 = createSeatEntity(UUID.randomUUID(), section1);
        Seat seat3 = createSeatEntity(UUID.randomUUID(), section2);

        EventSeat eventSeat1 = EventSeat.builder()
                .id(UUID.randomUUID())
                .eventSection(eventSection1)
                .seat(seat1)
                .status(EventSeatStatus.AVAILABLE)
                .price(new BigDecimal("1000.00"))
                .build();

        EventSeat eventSeat2 = EventSeat.builder()
                .id(UUID.randomUUID())
                .eventSection(eventSection1)
                .seat(seat2)
                .status(EventSeatStatus.SOLD)
                .price(new BigDecimal("1500.00"))
                .build();

        EventSeat eventSeat3 = EventSeat.builder()
                .id(UUID.randomUUID())
                .eventSection(eventSection2)
                .seat(seat3)
                .status(EventSeatStatus.AVAILABLE)
                .price(new BigDecimal("2000.00"))
                .build();

        when(eventRepository.findById(eventId))
                .thenReturn(Optional.of(event));

        when(eventSectionRepository.findAllByEventId(eventId))
                .thenReturn(List.of(eventSection1, eventSection2));

        when(eventSeatRepository.findAllByEventSectionIdIn(
                List.of(eventSection1.getId(), eventSection2.getId())
        )).thenReturn(List.of(
                eventSeat1,
                eventSeat2,
                eventSeat3
        ));

        // when
        EventDetailsResponseDto result = eventService.getEventById(eventId);

        // then
        assertEquals(2, result.getSections().size());

        EventSectionDetailsResponseDto sectionResponse1 =
                result.getSections().get(0);

        EventSectionDetailsResponseDto sectionResponse2 =
                result.getSections().get(1);

        assertEquals(2, sectionResponse1.getSeats().size());
        assertEquals(1, sectionResponse2.getSeats().size());

        assertEquals(eventSeat1.getId(), sectionResponse1.getSeats().get(0).getId());
        assertEquals(eventSeat2.getId(), sectionResponse1.getSeats().get(1).getId());
        assertEquals(eventSeat3.getId(), sectionResponse2.getSeats().get(0).getId());
    }

    @Test
    void updateEvent_shouldUpdateAllFields() {
        // given
        UUID eventId = UUID.randomUUID();

        Event event = createEventEntity(eventId);

        OffsetDateTime newStartAt =
                OffsetDateTime.parse("2026-11-01T18:00:00+03:00");
        OffsetDateTime newEndAt =
                OffsetDateTime.parse("2026-11-01T21:00:00+03:00");

        EventInfoRequestDto request = EventInfoRequestDto.builder()
                .name("New Concert Name")
                .description("New concert description")
                .startAt(newStartAt)
                .endAt(newEndAt)
                .build();

        EventUpdated updatedEvent = EventUpdated.builder()
                .eventId(eventId)
                .name("New Concert Name")
                .startAt(newStartAt)
                .endAt(newEndAt)
                .build();

        OutboxEvent outboxEvent = new OutboxEvent();

        when(eventRepository.findById(eventId))
                .thenReturn(Optional.of(event));

        when(outboxEventFactory.create(
                any(UUID.class),
                eq("EVENT_UPDATED"),
                eq("EVENT"),
                eq(eventId),
                any(EventUpdated.class)
        )).thenReturn(outboxEvent);

        // when
        EventSummaryResponseDto result =
                eventService.updateEvent(eventId, request);

        // then
        assertEquals("New Concert Name", event.getName());
        assertEquals("New concert description", event.getDescription());
        assertEquals(newStartAt, event.getStartAt());
        assertEquals(newEndAt, event.getEndAt());

        assertEquals(eventId, result.getId());
        assertEquals("New Concert Name", result.getName());
        assertEquals("New concert description", result.getDescription());
        assertEquals(newStartAt, result.getStartAt());
        assertEquals(newEndAt, result.getEndAt());

        verify(eventRepository).findById(eventId);
        verify(outboxEventFactory).create(
                any(UUID.class),
                eq("EVENT_UPDATED"),
                eq("EVENT"),
                eq(eventId),
                eq(updatedEvent)
        );
        verify(outboxEventRepository).save(outboxEvent);
    }


    @Test
    void updateEvent_whenOnlyNameProvided_shouldUpdateNameOnly() {
        // given
        UUID eventId = UUID.randomUUID();

        Event event = createEventEntity(eventId);

        String originalDescription = event.getDescription();
        OffsetDateTime originalStartAt = event.getStartAt();
        OffsetDateTime originalEndAt = event.getEndAt();

        EventInfoRequestDto request = EventInfoRequestDto.builder()
                .name("Updated Name")
                .build();

        OutboxEvent outboxEvent = new OutboxEvent();

        when(eventRepository.findById(eventId))
                .thenReturn(Optional.of(event));

        when(outboxEventFactory.create(
                any(UUID.class),
                eq("EVENT_UPDATED"),
                eq("EVENT"),
                eq(eventId),
                any(EventUpdated.class)
        )).thenReturn(outboxEvent);

        // when
        eventService.updateEvent(eventId, request);

        // then
        assertEquals("Updated Name", event.getName());
        assertEquals(originalDescription, event.getDescription());
        assertEquals(originalStartAt, event.getStartAt());
        assertEquals(originalEndAt, event.getEndAt());

        verify(outboxEventRepository).save(outboxEvent);
    }

    @Test
    void updateEvent_whenEventNotFound_shouldThrowException() {
        // given
        UUID eventId = UUID.randomUUID();

        EventInfoRequestDto request = EventInfoRequestDto.builder()
                .name("Updated Name")
                .build();

        when(eventRepository.findById(eventId))
                .thenReturn(Optional.empty());

        // when / then
        assertThrows(
                EventNotFoundException.class,
                () -> eventService.updateEvent(eventId, request)
        );

        verify(eventRepository).findById(eventId);
        verifyNoInteractions(outboxEventFactory, outboxEventRepository);
    }

    @Test
    void publishEvent_shouldPublishDraftEvent() {
        // given
        UUID eventId = UUID.randomUUID();

        Event event = createEventEntity(eventId);
        event.setStatus(EventStatus.DRAFT);

        when(eventRepository.findById(eventId))
                .thenReturn(Optional.of(event));

        // when
        EventSummaryResponseDto result =
                eventService.publishEvent(eventId);

        // then
        assertEquals(EventStatus.PUBLISHED, event.getStatus());
        assertEquals(eventId, result.getId());
        assertEquals(EventStatus.PUBLISHED, result.getStatus());

        verify(eventRepository).findById(eventId);
    }

    @Test
    void publishEvent_whenEventNotFound_shouldThrowException() {
        // given
        UUID eventId = UUID.randomUUID();

        when(eventRepository.findById(eventId))
                .thenReturn(Optional.empty());

        // when / then
        assertThrows(
                EventNotFoundException.class,
                () -> eventService.publishEvent(eventId)
        );

        verify(eventRepository).findById(eventId);
    }

    @ParameterizedTest
    @EnumSource(
            value = EventStatus.class,
            names = {"PUBLISHED", "CANCELLED", "FINISHED"}
    )
    void publishEvent_whenEventIsNotDraft_shouldThrowException(
            EventStatus status
    ) {
        // given
        UUID eventId = UUID.randomUUID();

        Event event = createEventEntity(eventId);
        event.setStatus(status);

        when(eventRepository.findById(eventId))
                .thenReturn(Optional.of(event));

        // when / then
        assertThrows(
                InvalidEventStateException.class,
                () -> eventService.publishEvent(eventId)
        );

        assertEquals(status, event.getStatus());

        verify(eventRepository).findById(eventId);
    }

    @Test
    void cancelEvent_shouldCancelEvent() {
        // given
        UUID eventId = UUID.randomUUID();

        Event event = createEventEntity(eventId);
        event.setStatus(EventStatus.DRAFT);

        when(eventRepository.findById(eventId))
                .thenReturn(Optional.of(event));

        // when
        EventSummaryResponseDto result =
                eventService.cancelEvent(eventId);

        // then
        assertEquals(EventStatus.CANCELLED, event.getStatus());
        assertEquals(eventId, result.getId());
        assertEquals(EventStatus.CANCELLED, result.getStatus());

        verify(eventRepository).findById(eventId);
    }

    @Test
    void cancelEvent_whenEventNotFound_shouldThrowException() {
        // given
        UUID eventId = UUID.randomUUID();

        when(eventRepository.findById(eventId))
                .thenReturn(Optional.empty());

        // when / then
        assertThrows(
                EventNotFoundException.class,
                () -> eventService.cancelEvent(eventId)
        );

        verify(eventRepository).findById(eventId);
    }

    @ParameterizedTest
    @EnumSource(
            value = EventStatus.class,
            names = {"CANCELLED", "FINISHED"}
    )
    void cancelEvent_whenEventCannotBeCancelled_shouldThrowException(
            EventStatus status
    ) {
        // given
        UUID eventId = UUID.randomUUID();

        Event event = createEventEntity(eventId);
        event.setStatus(status);

        when(eventRepository.findById(eventId))
                .thenReturn(Optional.of(event));

        // when / then
        assertThrows(
                InvalidEventStateException.class,
                () -> eventService.cancelEvent(eventId)
        );

        assertEquals(status, event.getStatus());

        verify(eventRepository).findById(eventId);
    }

    private Venue createVenueEntity(UUID venueId) {
        return Venue.builder()
                .id(venueId)
                .name("Arena")
                .description("Concert arena")
                .width(1000)
                .height(800)
                .build();
    }

    private Section createSectionEntity(UUID sectionId, Venue venue) {
        return Section.builder()
                .id(sectionId)
                .venue(venue)
                .name("VIP")
                .x(100)
                .y(100)
                .width(500)
                .height(300)
                .rotation(0)
                .build();
    }

    private Seat createSeatEntity(
            UUID seatId,
            Section section
    ) {
        return Seat.builder()
                .id(seatId)
                .section(section)
                .rowNumber(1)
                .seatNumber(1)
                .x(30)
                .y(200)
                .width(20)
                .height(20)
                .rotation(0)
                .build();
    }

    private Event createEventEntity(UUID eventId) {
        Venue venue = createVenueEntity(UUID.randomUUID());

        return Event.builder()
                .id(eventId)
                .venue(venue)
                .name("Rock Concert")
                .description("Rock concert description")
                .startAt(OffsetDateTime.parse("2026-10-01T19:00:00+03:00"))
                .endAt(OffsetDateTime.parse("2026-10-01T22:00:00+03:00"))
                .status(EventStatus.DRAFT)
                .eventType(EventType.CONCERT)
                .build();
    }
}