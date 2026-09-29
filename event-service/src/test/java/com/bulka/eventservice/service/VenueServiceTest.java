package com.bulka.eventservice.service;

import com.bulka.eventservice.dto.request.venue.SeatRequestDto;
import com.bulka.eventservice.dto.request.venue.SectionRequestDto;
import com.bulka.eventservice.dto.request.venue.VenueDetailsRequestDto;
import com.bulka.eventservice.dto.request.venue.VenueInfoRequestDto;
import com.bulka.eventservice.dto.response.venue.VenueDetailsResponseDto;
import com.bulka.eventservice.dto.response.venue.VenueSummaryResponseDto;
import com.bulka.eventservice.exception.venue.VenueNotFoundException;
import com.bulka.eventservice.mapper.venue.SeatMapper;
import com.bulka.eventservice.mapper.venue.SectionMapper;
import com.bulka.eventservice.mapper.venue.VenueMapper;
import com.bulka.eventservice.model.idempotency.IdempotencyKey;
import com.bulka.eventservice.model.idempotency.IdempotencyOperation;
import com.bulka.eventservice.model.venue.Seat;
import com.bulka.eventservice.model.venue.Section;
import com.bulka.eventservice.model.venue.Venue;
import com.bulka.eventservice.repository.IdempotencyKeyRepository;
import com.bulka.eventservice.repository.venue.SeatRepository;
import com.bulka.eventservice.repository.venue.SectionRepository;
import com.bulka.eventservice.repository.venue.VenueRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VenueServiceTest {
    @Mock VenueRepository venueRepository;
    @Mock SectionRepository sectionRepository;
    @Mock SeatRepository seatRepository;
    @Mock IdempotencyKeyRepository idempotencyKeyRepository;

    private VenueMapper venueMapper;
    private SectionMapper sectionMapper;
    private SeatMapper seatMapper;

    VenueService venueService;

    @BeforeEach
    void setUp() {
        venueMapper = new VenueMapper();
        sectionMapper = new SectionMapper();
        seatMapper = new SeatMapper();

        venueService = new VenueService(
                venueRepository,
                sectionRepository,
                seatRepository,
                idempotencyKeyRepository,
                venueMapper,
                sectionMapper,
                seatMapper
        );
    }

    @Test
    void createVenue_shouldCreateVenueSuccessfully() {
        VenueDetailsRequestDto request = createVenueRequest();

        when(idempotencyKeyRepository.insertIfAbsent(
                any(UUID.class),
                anyString(),
                eq(IdempotencyOperation.CREATE_VENUE.name()),
                any(UUID.class)
        )).thenReturn(1);

        when(venueRepository.save(any(Venue.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(sectionRepository.save(any(Section.class)))
                .thenAnswer(invocation -> {
                    Section section = invocation.getArgument(0);
                    section.setId(UUID.randomUUID());
                    return section;
                });

        when(seatRepository.saveAll(anyList()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // when
        VenueDetailsResponseDto result = venueService.createVenue(request, "test-key");

        // then
        assertNotNull(result);
        assertEquals("Test Arena", result.getName());
        assertEquals(1, result.getSections().size());
        assertEquals(2, result.getSections().get(0).getSeats().size());

        verify(idempotencyKeyRepository).insertIfAbsent(
                any(UUID.class),
                eq("test-key"),
                eq(IdempotencyOperation.CREATE_VENUE.name()),
                any(UUID.class)
        );

        verify(venueRepository).save(any(Venue.class));
        verify(sectionRepository).save(any(Section.class));
        verify(seatRepository).saveAll(anyList());
    }


    @Test
    void createVenue_whenIdempotencyKeyAlreadyExists_shouldReturnExistingVenue() {
        // given
        String idempotencyKey = "already-used-key";
        UUID existingVenueId = UUID.randomUUID();

        IdempotencyKey existingKey = IdempotencyKey.builder()
                .resourceId(existingVenueId)
                .build();

        Venue existingVenue = createVenueEntity(existingVenueId);
        Section section = createSectionEntity(existingVenue);
        Seat seat = createSeatEntity(section);

        when(idempotencyKeyRepository.insertIfAbsent(
                any(UUID.class),
                eq(idempotencyKey),
                eq(IdempotencyOperation.CREATE_VENUE.name()),
                any(UUID.class)
        )).thenReturn(0);

        when(idempotencyKeyRepository.findByKeyAndOperation(
                idempotencyKey,
                IdempotencyOperation.CREATE_VENUE
        )).thenReturn(Optional.of(existingKey));

        when(venueRepository.findById(existingVenueId))
                .thenReturn(Optional.of(existingVenue));

        when(sectionRepository.findAllByVenueId(existingVenueId))
                .thenReturn(List.of(section));

        when(seatRepository.findAllBySectionIdIn(
                List.of(section.getId())
        )).thenReturn(List.of(seat));

        // when
        VenueDetailsResponseDto result =
                venueService.createVenue(
                        createVenueRequest(),
                        idempotencyKey
                );

        // then
        assertNotNull(result);
        assertEquals(existingVenueId, result.getId());
        assertEquals("Existing Arena", result.getName());
        assertEquals(1, result.getSections().size());
        assertEquals(1, result.getSections().get(0).getSeats().size());

        verify(venueRepository, never())
                .save(any(Venue.class));

        verify(sectionRepository, never())
                .save(any(Section.class));

        verify(seatRepository, never())
                .saveAll(anyList());
    }

    @Test
    void createVenue_shouldSaveVenueWithCorrectData() {
        // given
        VenueDetailsRequestDto request = createVenueRequest();

        when(idempotencyKeyRepository.insertIfAbsent(
                any(UUID.class),
                anyString(),
                eq(IdempotencyOperation.CREATE_VENUE.name()),
                any(UUID.class)
        )).thenReturn(1);

        when(venueRepository.save(any(Venue.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(sectionRepository.save(any(Section.class)))
                .thenAnswer(invocation -> {
                    Section section = invocation.getArgument(0);
                    section.setId(UUID.randomUUID());
                    return section;
                });

        when(seatRepository.saveAll(anyList()))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // when
        venueService.createVenue(request, "test-key");

        // then
        verify(venueRepository).save(argThat(venue ->
                venue.getName().equals("Test Arena")
                        && venue.getDescription().equals("Test description")
                        && venue.getWidth() == 1000
                        && venue.getHeight() == 800
        ));
    }

    @Test
    void createVenue_whenVenueSaveFails_shouldNotCreateSections() {
        // given
        VenueDetailsRequestDto request = createVenueRequest();

        when(idempotencyKeyRepository.insertIfAbsent(
                any(UUID.class),
                anyString(),
                eq(IdempotencyOperation.CREATE_VENUE.name()),
                any(UUID.class)
        )).thenReturn(1);

        when(venueRepository.save(any(Venue.class)))
                .thenThrow(new RuntimeException("Database error"));

        // when / then
        assertThrows(
                RuntimeException.class,
                () -> venueService.createVenue(request, "test-key")
        );

        verify(sectionRepository, never())
                .save(any(Section.class));

        verify(seatRepository, never())
                .saveAll(anyList());
    }

    @Test
    void getAllVenuesSummary_shouldReturnAllVenues() {
        // given
        Venue venue1 = createVenueEntity(UUID.randomUUID());
        Venue venue2 = createVenueEntity(UUID.randomUUID());

        venue2.setName("Second Arena");
        venue2.setDescription("Second description");

        when(venueRepository.findAll())
                .thenReturn(List.of(venue1, venue2));

        // when
        List<VenueSummaryResponseDto> result =
                venueService.getAllVenuesSummary();

        // then
        assertNotNull(result);
        assertEquals(2, result.size());

        assertEquals(venue1.getId(), result.get(0).getId());
        assertEquals(venue1.getName(), result.get(0).getName());

        assertEquals(venue2.getId(), result.get(1).getId());
        assertEquals(venue2.getName(), result.get(1).getName());

        verify(venueRepository).findAll();
    }

    @Test
    void getAllVenuesSummary_whenNoVenues_shouldReturnEmptyList() {
        // given
        when(venueRepository.findAll())
                .thenReturn(List.of());

        // when
        List<VenueSummaryResponseDto> result =
                venueService.getAllVenuesSummary();

        // then
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(venueRepository).findAll();
    }

    @Test
    void getAllVenuesSummary_whenRepositoryFails_shouldThrowException() {
        // given
        when(venueRepository.findAll())
                .thenThrow(new RuntimeException("Database error"));

        // when / then
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> venueService.getAllVenuesSummary()
        );

        assertEquals("Database error", exception.getMessage());

        verify(venueRepository).findAll();
    }

    @Test
    void getVenueDetailsById_shouldReturnVenueWithSectionsAndSeats() {
        // given
        UUID venueId = UUID.randomUUID();

        Venue venue = createVenueEntity(venueId);

        Section vipSection = createSectionEntity(venue);
        Section standardSection = createSectionEntity(venue);

        Seat vipSeat = createSeatEntity(vipSection);
        Seat standardSeat = createSeatEntity(standardSection);

        when(venueRepository.findById(venueId))
                .thenReturn(Optional.of(venue));

        when(sectionRepository.findAllByVenueId(venueId))
                .thenReturn(List.of(vipSection, standardSection));

        when(seatRepository.findAllBySectionIdIn(
                List.of(vipSection.getId(), standardSection.getId())
        )).thenReturn(List.of(vipSeat, standardSeat));

        // when
        VenueDetailsResponseDto result =
                venueService.getVenueDetailsById(venueId);

        // then
        assertNotNull(result);
        assertEquals(venueId, result.getId());
        assertEquals(venue.getName(), result.getName());

        assertEquals(2, result.getSections().size());

        assertEquals(1, result.getSections().get(0).getSeats().size());
        assertEquals(1, result.getSections().get(1).getSeats().size());

        verify(venueRepository).findById(venueId);
        verify(sectionRepository).findAllByVenueId(venueId);

        verify(seatRepository).findAllBySectionIdIn(
                List.of(vipSection.getId(), standardSection.getId())
        );
    }

    @Test
    void getVenueDetailsById_whenVenueNotFound_shouldThrowException() {
        // given
        UUID venueId = UUID.randomUUID();

        when(venueRepository.findById(venueId))
                .thenReturn(Optional.empty());

        // when / then
        VenueNotFoundException exception = assertThrows(
                VenueNotFoundException.class,
                () -> venueService.getVenueDetailsById(venueId)
        );

        assertEquals(
                "Venue with id " + venueId + " not found",
                exception.getMessage()
        );

        verify(venueRepository).findById(venueId);

        verify(sectionRepository, never())
                .findAllByVenueId(any(UUID.class));

        verify(seatRepository, never())
                .findAllBySectionIdIn(anyList());
    }

    @Test
    void getVenueDetailsById_whenSectionsHaveNoSeats_shouldReturnEmptySeatLists() {
        // given
        UUID venueId = UUID.randomUUID();

        Venue venue = createVenueEntity(venueId);
        Section section = createSectionEntity(venue);

        when(venueRepository.findById(venueId))
                .thenReturn(Optional.of(venue));

        when(sectionRepository.findAllByVenueId(venueId))
                .thenReturn(List.of(section));

        when(seatRepository.findAllBySectionIdIn(
                List.of(section.getId())
        )).thenReturn(List.of());

        // when
        VenueDetailsResponseDto result =
                venueService.getVenueDetailsById(venueId);

        // then
        assertNotNull(result);
        assertEquals(1, result.getSections().size());
        assertNotNull(result.getSections().get(0).getSeats());
        assertTrue(result.getSections().get(0).getSeats().isEmpty());

        verify(venueRepository).findById(venueId);
        verify(sectionRepository).findAllByVenueId(venueId);
        verify(seatRepository).findAllBySectionIdIn(
                List.of(section.getId())
        );
    }

    @Test
    void getVenueDetailsById_whenVenueHasNoSections_shouldReturnEmptySections() {
        // given
        UUID venueId = UUID.randomUUID();

        Venue venue = createVenueEntity(venueId);

        when(venueRepository.findById(venueId))
                .thenReturn(Optional.of(venue));

        when(sectionRepository.findAllByVenueId(venueId))
                .thenReturn(List.of());

        when(seatRepository.findAllBySectionIdIn(List.of()))
                .thenReturn(List.of());

        // when
        VenueDetailsResponseDto result =
                venueService.getVenueDetailsById(venueId);

        // then
        assertNotNull(result);
        assertEquals(venueId, result.getId());
        assertNotNull(result.getSections());
        assertTrue(result.getSections().isEmpty());

        verify(venueRepository).findById(venueId);
        verify(sectionRepository).findAllByVenueId(venueId);
        verify(seatRepository).findAllBySectionIdIn(List.of());
    }

    @Test
    void updateVenueInfo_shouldUpdateNameAndDescription() {
        // given
        UUID venueId = UUID.randomUUID();

        Venue venue = createVenueEntity(venueId);

        VenueInfoRequestDto request = VenueInfoRequestDto.builder()
                .name("Updated Arena")
                .description("Updated description")
                .build();

        when(venueRepository.findById(venueId))
                .thenReturn(Optional.of(venue));

        // when
        VenueSummaryResponseDto result =
                venueService.updateVenueInfo(venueId, request);

        // then
        assertNotNull(result);

        assertEquals("Updated Arena", venue.getName());
        assertEquals("Updated description", venue.getDescription());

        assertEquals("Updated Arena", result.getName());

        verify(venueRepository).findById(venueId);
    }


    @Test
    void updateVenueInfo_whenOnlyNameProvided_shouldUpdateNameOnly() {
        // given
        UUID venueId = UUID.randomUUID();

        Venue venue = createVenueEntity(venueId);
        String originalDescription = venue.getDescription();

        VenueInfoRequestDto request = VenueInfoRequestDto.builder()
                .name("Updated Arena")
                .build();

        when(venueRepository.findById(venueId))
                .thenReturn(Optional.of(venue));

        // when
        VenueSummaryResponseDto result =
                venueService.updateVenueInfo(venueId, request);

        // then
        assertEquals("Updated Arena", venue.getName());
        assertEquals(originalDescription, venue.getDescription());

        assertEquals("Updated Arena", result.getName());

        verify(venueRepository).findById(venueId);
    }

    @Test
    void updateVenueInfo_whenOnlyDescriptionProvided_shouldUpdateDescriptionOnly() {
        // given
        UUID venueId = UUID.randomUUID();

        Venue venue = createVenueEntity(venueId);
        String originalName = venue.getName();

        VenueInfoRequestDto request = VenueInfoRequestDto.builder()
                .description("Updated description")
                .build();

        when(venueRepository.findById(venueId))
                .thenReturn(Optional.of(venue));

        // when
        VenueSummaryResponseDto result =
                venueService.updateVenueInfo(venueId, request);

        // then
        assertEquals(originalName, venue.getName());
        assertEquals("Updated description", venue.getDescription());

        assertEquals(originalName, result.getName());

        verify(venueRepository).findById(venueId);
    }

    @Test
    void updateVenueInfo_whenVenueNotFound_shouldThrowException() {
        // given
        UUID venueId = UUID.randomUUID();

        VenueInfoRequestDto request = VenueInfoRequestDto.builder()
                .name("Updated Arena")
                .description("Updated description")
                .build();

        when(venueRepository.findById(venueId))
                .thenReturn(Optional.empty());

        // when / then
        VenueNotFoundException exception = assertThrows(
                VenueNotFoundException.class,
                () -> venueService.updateVenueInfo(venueId, request)
        );

        assertEquals(
                "Venue with id " + venueId + " not found",
                exception.getMessage()
        );

        verify(venueRepository).findById(venueId);
    }

    private Venue createVenueEntity(UUID id) {
        return Venue.builder()
                .id(id)
                .name("Existing Arena")
                .description("Existing description")
                .width(1000)
                .height(800)
                .build();
    }
    private Section createSectionEntity(Venue venue) {
        return Section.builder()
                .id(UUID.randomUUID())
                .venue(venue)
                .name("VIP")
                .x(100)
                .y(100)
                .width(500)
                .height(300)
                .rotation(0)
                .build();
    }
    private Seat createSeatEntity(Section section) {
        return Seat.builder()
                .id(UUID.randomUUID())
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
    private VenueDetailsRequestDto createVenueRequest() {
        SeatRequestDto seat1 = createSeatRequest(1, 1);
        SeatRequestDto seat2 = createSeatRequest(1, 2);

        SectionRequestDto section = SectionRequestDto.builder()
                .name("VIP")
                .x(100)
                .y(100)
                .width(500)
                .height(300)
                .rotation(0)
                .seats(List.of(seat1, seat2))
                .build();

        return VenueDetailsRequestDto.builder()
                .name("Test Arena")
                .description("Test description")
                .width(1000)
                .height(800)
                .sections(List.of(section))
                .build();
    }

    private SeatRequestDto createSeatRequest(int rowNumber, int seatNumber) {
        return  SeatRequestDto.builder()
            .rowNumber(rowNumber)
            .seatNumber(seatNumber)
            .x(seatNumber * 30)
            .y(200)
            .width(20)
            .height(20)
            .rotation(0)
            .build();
    }
}