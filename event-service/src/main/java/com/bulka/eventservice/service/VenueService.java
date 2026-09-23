package com.bulka.eventservice.service;

import com.bulka.eventservice.dto.request.venue.SectionRequestDto;
import com.bulka.eventservice.dto.request.venue.VenueDetailsRequestDto;
import com.bulka.eventservice.dto.request.venue.VenueInfoRequestDto;
import com.bulka.eventservice.dto.response.venue.SeatResponseDto;
import com.bulka.eventservice.dto.response.venue.SectionResponseDto;
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
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class VenueService {
    private final VenueRepository venueRepository;
    private final SectionRepository sectionRepository;
    private final SeatRepository seatRepository;
    private final IdempotencyKeyRepository idempotencyKeyRepository;

    private final VenueMapper venueMapper;
    private final SectionMapper sectionMapper;
    private final SeatMapper seatMapper;

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Transactional
    public VenueDetailsResponseDto createVenue(VenueDetailsRequestDto request, String idempotencyKey){

        UUID venueId = UUID.randomUUID();
        int inserted = idempotencyKeyRepository.insertIfAbsent(
                UUID.randomUUID(),
                idempotencyKey,
                IdempotencyOperation.CREATE_VENUE.name(),
                venueId
        );

        if(inserted == 0){
            IdempotencyKey key = idempotencyKeyRepository
                    .findByKeyAndOperation(idempotencyKey, IdempotencyOperation.CREATE_VENUE)
                    .orElseThrow(() -> new IllegalStateException("Idempotency key not found"));

            return getVenueDetailsById(key.getResourceId());
        }

        Venue venue = venueMapper.toEntity(venueId, request);
        Venue savedVenue = venueRepository.save(venue);

        List<SectionResponseDto> sections = request.getSections()
                .stream()
                .map(sectionDto -> createSection(
                        savedVenue,
                        sectionDto)
                )
                .toList();

        return venueMapper.toDetailsResponse(savedVenue, sections);
    }

    private SectionResponseDto createSection(Venue venue, SectionRequestDto request){
        Section section = sectionMapper.toEntity(venue, request);

        Section savedSection = sectionRepository.save(section);
        List<Seat> seats = request.getSeats()
                .stream()
                .map(seatRequest -> seatMapper.toEntity(
                        savedSection,
                        seatRequest
                        )
                )
                .toList();

        List<Seat> savedSeats = seatRepository.saveAll(seats);

        List<SeatResponseDto> seatResponses = savedSeats.stream()
                .map(seatMapper::toResponse)
                .toList();

        return sectionMapper.toResponse(savedSection, seatResponses);
    }

    @Transactional(readOnly = true)
    public List<VenueSummaryResponseDto> getAllVenuesSummary(){
        List<Venue> allVenues = venueRepository.findAll();
        return allVenues
                .stream()
                .map(venueMapper::toVenueSummary)
                .toList();
    }

    @Transactional(readOnly = true)
    public VenueDetailsResponseDto getVenueDetailsById(UUID venueId){
        Venue venue =  venueRepository.findById(venueId).orElseThrow(() ->
                new VenueNotFoundException("Venue with id " + venueId + " not found"));

        List<Section> sections = sectionRepository.findAllByVenueId(venue.getId());

        List<Seat> allSeatsOfSections = seatRepository.findAllBySectionIdIn(sections
                .stream()
                .map(Section::getId)
                .toList()
        );

        Map<UUID, List<Seat>> seatsBySectionId = allSeatsOfSections.stream()
                .collect(Collectors.groupingBy(
                        seat -> seat.getSection().getId()
                ));

        List<SectionResponseDto> sectionResponses = sections.stream()
                .map(section -> {
                    List<SeatResponseDto> seatResponses = seatsBySectionId.
                            getOrDefault(section.getId(), List.of())
                            .stream()
                            .map(seatMapper::toResponse)
                            .toList();

                    return sectionMapper.toResponse(section, seatResponses);
                })
                .toList();


        return venueMapper.toDetailsResponse(venue, sectionResponses);
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Transactional
    public VenueSummaryResponseDto updateVenueInfo(UUID venueId, VenueInfoRequestDto request){
        Venue venue =  venueRepository.findById(venueId).orElseThrow(() ->
                new VenueNotFoundException("Venue with id " + venueId + " not found"));

        if(request.getName() != null){
            venue.setName(request.getName());
        }
        if(request.getDescription() != null){
            venue.setDescription(request.getDescription());
        }

        return venueMapper.toVenueSummary(venue);
    }


//    @Transactional
//    public VenueInfoResponseDto updateVenue(UUID venueId, VenueInfoRequestDto venueInfoRequestDto) {
//        Venue venue = venueRepository.findById(venueId).orElseThrow(() ->
//                new VenueNotFoundException("Venue with id " + venueId + " not found"));
//
//        if(venueInfoRequestDto.getName() != null){
//            venue.setName(venueInfoRequestDto.getName());
//        }
//        if(venueInfoRequestDto.getDescription() != null){
//            venue.setDescription(venueInfoRequestDto.getDescription());
//        }
//        if(venueInfoRequestDto.getWidth() != null){
//            venue.setWidth(venueInfoRequestDto.getWidth());
//        }
//        if(venueInfoRequestDto.getHeight() != null){
//            venue.setHeight(venueInfoRequestDto.getHeight());
//        }
//
//        return venueMapper.toVenueInfo(venue);
//    }

//    @Transactional
//    public void deleteVenue(UUID venueId){
//        venueRepository.deleteById(venueId);
//    }

//    @Transactional(readOnly = true)
//    public List<SeatResponseDto> getSeatsByVenueId(UUID venueId){
//        Venue venue = venueRepository.findById(venueId).orElseThrow(() ->
//                new VenueNotFoundException("Venue with id " + venueId + " not found"));
//        List<Seat> seats = seatRepository.findAllByVenueId(venueId);
//
//        return seats.stream()
//                .map(seat -> venueMapper.toSeatResponse(venue, seat))
//                .toList();
//
//    }
}
